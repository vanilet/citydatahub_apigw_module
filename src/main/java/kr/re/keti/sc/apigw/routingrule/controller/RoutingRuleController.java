package kr.re.keti.sc.apigw.routingrule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.re.keti.sc.apigw.common.ManagementInterfaceSecurity;
import kr.re.keti.sc.apigw.protocol.http.AccessTokenHttpUtils;
import kr.re.keti.sc.apigw.protocol.http.HttpConstants;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRule;
import kr.re.keti.sc.apigw.routingrule.service.RoutingRuleService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


@Slf4j
@Component
@RestControllerEndpoint(id= "routes")
public class RoutingRuleController {
	
	@Autowired
	ManagementInterfaceSecurity managementInterfaceSecurity;

	@Autowired
	private RoutingRuleService routingRuleService;

	/* 전체 라우팅 룰 조회 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	private Flux <RoutingRule> selectAllRoutes(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue)
			throws Exception {
		log.debug("'selectAllRoutes' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		return routingRuleService.retrieveAllRouteRules();
	}
	
	/* 개별 라우팅 룰 조회 */
	@RequestMapping(value = "/{" + HttpConstants.Path.ROUTING_RULE_ID +"}", method = RequestMethod.GET)
	@ResponseBody
	private RoutingRule selectRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue,
			@PathVariable(HttpConstants.Path.ROUTING_RULE_ID) String routingRuleId)
			throws Exception {
		log.debug("'selectRoute' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		return routingRuleService.retrieveRouteRule(routingRuleId);
	}
	
	/* 개별 라우팅 룰 생성 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	private void createRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue, 
			@RequestBody RoutingRule routingRule)
			throws Exception {
		log.debug("'createRoute' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		routingRuleService.createRouteRule(routingRule);
	}
	
	/* 개별 라우팅 룰 갱신 */
	@RequestMapping(value = "/{" + HttpConstants.Path.ROUTING_RULE_ID +"}", method = RequestMethod.PUT)
	@ResponseBody
	private void updateRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue,
			@PathVariable(HttpConstants.Path.ROUTING_RULE_ID) String routingRuleId,
			@RequestBody RoutingRule routingRule)
			throws Exception {
		log.debug("'updateRoute' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		routingRule.setId(routingRuleId);
		routingRuleService.updateRouteRule(routingRule);
	}
	
	/* 개별 라우팅 룰 삭제 */
	@RequestMapping(value = "/{" + HttpConstants.Path.ROUTING_RULE_ID +"}", method = RequestMethod.DELETE)
	@ResponseBody
	private void deleteRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue, 
			@PathVariable(HttpConstants.Path.ROUTING_RULE_ID) String routingRuleId)
			throws Exception {
		log.debug("'deleteRoute' has been called");
		
		managementInterfaceSecurity.apply(authorizationHeaderValue);
		RoutingRule routingRule = new RoutingRule();
		routingRule.setId(routingRuleId);
		routingRuleService.deleteRouteRule(routingRule);
	}
	
	/* 라우팅 룰 Refresh */
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	@ResponseBody
	private void refreshRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue
			) throws Exception {
		log.debug("'refreshRoute' has been called");
		
		routingRuleService.refreshRouteRule();
	}

	/* 라우팅 룰 Propagate */
	@RequestMapping(value = "/propagate", method = RequestMethod.POST)
	@ResponseBody
	private void propagateRoute(
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue
			) throws Exception {
		log.debug("'propagateToRoute' has been called");
		
		routingRuleService.propagateRouteRule();
	}
}
