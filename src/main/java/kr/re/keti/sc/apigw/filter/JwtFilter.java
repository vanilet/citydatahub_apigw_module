package kr.re.keti.sc.apigw.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import kr.re.keti.sc.apigw.common.AccessTokenVerifier;
import kr.re.keti.sc.apigw.filter.model.AccessTokenInfo;
import kr.re.keti.sc.apigw.filter.model.FilterOrderContstant;
import kr.re.keti.sc.apigw.protocol.http.AccessTokenHttpUtils;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRule;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

	private static final String APPLYED_ROUTING_KEY = "org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRoute";
	
	@Autowired
	private AccessTokenVerifier accessTokenVerifier;
	
	@Component
	public static class Config {
		private Map <String, RoutingRule> routingRuleMap = new HashMap<String, RoutingRule> ();
		public Map<String, RoutingRule> getRoutingRuleMap () {
			return routingRuleMap;
		}
		public void configureRoutingRuleMap (List<RoutingRule> routingRules) {
			routingRuleMap.clear();
			for (RoutingRule routingrule: routingRules) {
				routingRuleMap.put(routingrule.getId(), routingrule);
			}
		}
	}

	public List<String> obtainAuthorizationRoles (ServerWebExchange exchange, Config config) {
		//1. Routing이 적용된 Spring Cloud Gateway Route 획득
		Route route = (Route) exchange.getAttribute(APPLYED_ROUTING_KEY);
		
		//2. Route ID로 RoutingRule 획득 
		RoutingRule routingRule = config.getRoutingRuleMap().get(route.getId());
		
		//3. RoutingRule에서 해당 Route에 대해 허용된 Access Role들 반환
		return routingRule.getAuthorizationRoleIds();
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter((exchange, chain) -> {
			log.debug("accessTokenVerifyFilter called");

			// 1. Check Existence of Authorization Header && Bearer Type
			String accessTokenString = AccessTokenHttpUtils.extractAccessToken(exchange.getRequest());

			// 2. Decrypt 또는 Opaque 토큰 처리 (e.g., JWE)

			// 3. Check Blacklist
			
			// 4. Obtain Allowed Authorization Roles
			List<String> allowedAuthorizedRoles = obtainAuthorizationRoles(exchange, config);

			// 5. verify and obtain accessToken
			AccessTokenInfo.Payload accessTokenPayload = accessTokenVerifier.verify(accessTokenString, allowedAuthorizedRoles);

			// 6. add header
			AccessTokenHttpUtils.addAccessTokenHeader(exchange, accessTokenPayload.getRawPayload());
			
			// 7. add attributes
			if (accessTokenPayload.getAud() != null) {
				exchange.getAttributes().put(AccessTokenInfo.Payload.AUD, accessTokenPayload.getAud());
			}
			if (accessTokenPayload.getUserId() != null) {
				exchange.getAttributes().put(AccessTokenInfo.Payload.USER_ID, accessTokenPayload.getUserId());
			}
			return chain.filter(exchange);
		},FilterOrderContstant.JWTFILTER);
	}
}