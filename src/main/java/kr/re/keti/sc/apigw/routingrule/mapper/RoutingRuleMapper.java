package kr.re.keti.sc.apigw.routingrule.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.re.keti.sc.apigw.routingrule.model.RoutingRule;

@Repository
public interface RoutingRuleMapper {
	List<RoutingRule> selectAllRouteRules ();
	
	List<RoutingRule> selectAllRouteRulesForConfiguringRoutes ();

	RoutingRule selectRouteRule (String routingRuleId);

	void insertRouteRule (RoutingRule routingRule);

	int updateRouteRule (RoutingRule routingRule);

	int deleteRouteRule (RoutingRule routingRule);
}