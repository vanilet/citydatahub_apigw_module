package kr.re.keti.sc.apigw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.BooleanSpec;
import org.springframework.cloud.gateway.route.builder.BooleanSpec.BooleanOpSpec;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import kr.re.keti.sc.apigw.filter.JwtFilter;
import kr.re.keti.sc.apigw.filter.LocalRateLimitFilter;
import kr.re.keti.sc.apigw.filter.model.RateLimit;
import kr.re.keti.sc.apigw.routingrule.model.LogicalOperation;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRule;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRule.RoutingRuleDetail;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRuleApplicationEvent;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRuleDetailAttributeOperation;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRuleDetailType;
import kr.re.keti.sc.apigw.routingrule.service.RoutingRuleService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class WholeUpdateRouteLocator implements RouteLocator, ApplicationListener<RoutingRuleApplicationEvent> {

    private RouteLocatorBuilder builder;
    private RouteLocatorBuilder.Builder routesBuilder;
    private Flux<Route> route;
   
    @Value("${clientRateLimit.scope}")
	private String rateLimitScopeString;

	@Value("${clientRateLimit.type}")
	private String rateLimitTypeString;

	@Value("${routingRetry.retryValue}")
	private int retryValue;

	private RoutingRuleService routingRuleService;

    @Autowired
    private RedisRateLimiter redisRateLimiter;

    @Autowired
    private LocalRateLimitFilter localRateLimitFilter;

    @Autowired
    private LocalRateLimitFilter.Config localRateLimitConfig;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
	private JwtFilter.Config jwtConfig;
	
	private Map <String, Consumer<SpringCloudCircuitBreakerFilterFactory.Config>> circuitBreakerPerService = new HashMap <String, Consumer<SpringCloudCircuitBreakerFilterFactory.Config>> ();

	@Autowired
	public WholeUpdateRouteLocator(RouteLocatorBuilder builder, RoutingRuleService routingRuleService) {
		this.builder = builder;
		this.routingRuleService = routingRuleService;
	}

    @PostConstruct
    public void init(){
    	loadRoutes();
    }

    /**
     * Remove all routes.
     */
    public void clearRoutes() {
        routesBuilder = builder.routes();
    }
	
    public void buildRoutes() {
        this.route = routesBuilder.build().getRoutes();
    }
    
    @Override
    public Flux<Route> getRoutes() {
        return route;
    }
    
    private void configureRoutes (List <RoutingRule> routingRules) {
    	
    	jwtConfig.configureRoutingRuleMap(routingRules);

    	for (RoutingRule routingRule : routingRules) {
			try {
				
				this.routesBuilder = this.routesBuilder.route(routingRule.getId(), p -> {

					BooleanSpec currentBooleanSpec = null;
					for (RoutingRuleDetail routingRuleDetail : routingRule.getRoutingRuleDetails()) {

						if (RoutingRuleDetailType.Path.equals(routingRuleDetail.getType())) {
							BooleanOpSpec booleanOpSpec = getBooleanOpSpecWithLogicalOperation(currentBooleanSpec, routingRuleDetail.getLogicalOperation());

							String resolveRelativePath = resolveRelativePath(routingRuleDetail.getAttributeValue());
							if (currentBooleanSpec == null) {
								currentBooleanSpec = p.path(resolveRelativePath);
							} else {
								currentBooleanSpec = booleanOpSpec.path(resolveRelativePath);
							}

						} else if (RoutingRuleDetailType.Header.equals(routingRuleDetail.getType())) {
							BooleanOpSpec booleanOpSpec = getBooleanOpSpecWithLogicalOperation(currentBooleanSpec, routingRuleDetail.getLogicalOperation());

							if (currentBooleanSpec == null) {
								currentBooleanSpec = p.header(routingRuleDetail.getAttributeName(), routingRuleDetail.getAttributeValue());
							} else {
								currentBooleanSpec = booleanOpSpec.header(routingRuleDetail.getAttributeName(), routingRuleDetail.getAttributeValue());
							}

						} else if (RoutingRuleDetailType.QueryString.equals(routingRuleDetail.getType())) {
							BooleanOpSpec booleanOpSpec = getBooleanOpSpecWithLogicalOperation(currentBooleanSpec, routingRuleDetail.getLogicalOperation());

							if (currentBooleanSpec == null) {
								currentBooleanSpec = p.query(routingRuleDetail.getAttributeName(), routingRuleDetail.getAttributeValue());
							} else {
								currentBooleanSpec = booleanOpSpec.query(routingRuleDetail.getAttributeName(), routingRuleDetail.getAttributeValue());
							}
						}

						if (RoutingRuleDetailAttributeOperation.NotEqual.equals(routingRuleDetail.getAttributeOperation())) {
							currentBooleanSpec = currentBooleanSpec.negate();
						}
					}

					if (currentBooleanSpec != null) {
						currentBooleanSpec.filters(f ->  {
							//인가 토큰 확인 필요 시
							if(routingRule.getAuthorizationYn() != null && routingRule.getAuthorizationYn()) {
								f.filter(jwtFilter.apply(jwtConfig));
							}
							//Rate Limit Filter 설정
							setRateLimitFilter(f);
							
							// Circuit Breaker 설정
							// 서비스 별로 Circuit Breaker를 할당하기 위해 Consumer를 서비스 별로 저장하여 사용. 테스트 기준으로는 정상 동작하는 것으로 보임 
							if (routingRule.getTargetService() != null && routingRule.getTargetService().getFallbackPath() != null && !routingRule.getTargetService().getFallbackPath().isBlank()) {
								if (circuitBreakerPerService.get(routingRule.getTargetService().getId()) != null) {
									f.circuitBreaker(circuitBreakerPerService.get(routingRule.getTargetService().getId()));
								} else {
									Consumer <SpringCloudCircuitBreakerFilterFactory.Config> consumer = c -> c.setName(routingRule.getTargetService().getId()).setFallbackUri(resolveRelativePath(routingRule.getTargetService().getFallbackPath()));
									circuitBreakerPerService.put(routingRule.getTargetService().getId(), consumer);
									f.circuitBreaker(consumer);
								}
							}
							
							// Retry 값 설정
							f.retry(c -> c.allMethods().setRetries(retryValue));

							return f;
						});
						
						if(routingRule.getMethod() != null) {
							currentBooleanSpec.and().method(routingRule.getMethod());
						}
						
						String newPathValue = resolveRelativePath(routingRule.getNewPath());
						if (newPathValue != null) {
							currentBooleanSpec.filters(f -> f.setPath(newPathValue));
						}
						
						return currentBooleanSpec.uri(routingRule.getTargetService().resolveUri());
					} else {
						return null;
					}
				});

			} catch (Exception e) {
  				log.error("Unexpected Error Orrured.", e);
			}
		}
    }

	public void loadRoutes() {
		log.info("loading routingRules from Database");
    	//1. Get All Routes Info from RoutingRuleService
		List <RoutingRule> routingRules = routingRuleService.retrieveAllRouteRulesWithService();
		log.debug("routingRules : " + routingRules);
		
		//2. Clear Routes
		clearRoutes();
		
		//3. Set Routes
		configureRoutes(routingRules);
		
		//4. Build Routes
		buildRoutes();
    }
    
    public void refreshRoutes() {
    	loadRoutes();
    }

	private GatewayFilterSpec setRateLimitFilter(GatewayFilterSpec f) {
		if (RateLimit.Type.fromTypeString(rateLimitTypeString) == null ||
				RateLimit.Scope.fromScopeString(rateLimitScopeString) == null) {//Invalid Setting
			return f;
		} 
		if (RateLimit.Scope.Cluster.equals(RateLimit.Scope.fromScopeString(rateLimitScopeString))) {
			f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter));
		} else {
			f.filter(localRateLimitFilter.apply(localRateLimitConfig));
		}
		return f;
	}


    private BooleanOpSpec getBooleanOpSpecWithLogicalOperation (BooleanSpec booleanSpec, LogicalOperation logicalOperation) {
    	if (booleanSpec == null) {
			return null;
		}
    	
    	if (doesLogicalOperationExist(logicalOperation)) {
		
    		if (LogicalOperation.AND.equals(logicalOperation)) {
				return booleanSpec.and();
			}
			
			if (LogicalOperation.OR.equals(logicalOperation)) {
				return booleanSpec.or();
			}
		}
		
		//Default Policy: AND
		return booleanSpec.and();
	}
	
	private Boolean doesLogicalOperationExist(LogicalOperation logicalOperation) {
		return logicalOperation != null;
	}
	
	private static final String PATH_DELIMETER_STRING = "/";
	
	private String resolveRelativePath (String path) {
		if (path == null || path.trim().isEmpty()) return null;
		
		//Add First Character
		if (!path.startsWith(PATH_DELIMETER_STRING)) {
			path = new StringBuilder().append(PATH_DELIMETER_STRING).append(path).toString();
		}
		
		//Remove Last Character
		if (path.length() >= PATH_DELIMETER_STRING.length() && path.endsWith(PATH_DELIMETER_STRING)) {
			path = path.substring(0, path.length() - PATH_DELIMETER_STRING.length());
		}
		
		if (path == null || path.trim().isEmpty()) return null;
		
		return path;
	}

	@Override
	public void onApplicationEvent(RoutingRuleApplicationEvent event) {
		log.debug("RoutingRuleApplicationEvent has occured. Event Info: " + event);
		refreshRoutes();
	}
}
