package kr.re.keti.sc.apigw.routingrule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.re.keti.sc.apigw.common.exception.BadRequestException;
import kr.re.keti.sc.apigw.common.exception.NotFoundException;
import kr.re.keti.sc.apigw.routingrule.RoutingRuleEventPublisher;
import kr.re.keti.sc.apigw.routingrule.mapper.RoutingRuleMapper;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRule;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRuleApplicationEvent.RoutingRuleEvent;
import kr.re.keti.sc.apigw.routingrule.model.RoutingRuleApplicationEvent.RoutingRuleEvent.EventType;
import reactor.core.publisher.Flux;
 
@Service
public class RoutingRuleService {
	
	@Autowired
	private RoutingRuleMapper routingRuleMapper;

	@Autowired
	private PropagateService propagateService;
	
	@Autowired
	private RoutingRuleEventPublisher routingRuleEventPublisher;
	
	public List <RoutingRule> retrieveAllRouteRulesWithService () {
		return routingRuleMapper.selectAllRouteRulesForConfiguringRoutes();
	}
	
	public Flux <RoutingRule> retrieveAllRouteRules () {
		return Flux.fromIterable(routingRuleMapper.selectAllRouteRules());
	}
	
	public RoutingRule retrieveRouteRule (String routingRuleId) {
		RoutingRule routingRule = routingRuleMapper.selectRouteRule(routingRuleId);
		if (routingRule == null) {
			throw new NotFoundException(String.format("Specified 'id': %s does not Exist", routingRuleId));
		}
		return routingRule;
	}
	
	public void createRouteRule(RoutingRule routingRule) {
		validateRoutingRule(routingRule);
		
		try {
			routingRuleMapper.insertRouteRule(routingRule);
		} catch (Exception e) {
			if (e instanceof org.springframework.dao.DuplicateKeyException) {
				throw new BadRequestException(String.format("Specified 'id': %s is duplicated", routingRule.getId()));
			}
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				throw new BadRequestException(String.format("Specified 'targetServiceId': %s does not Exist", routingRule.getTargetServiceId()));
			}
		}
		
		propagateService.propagateRoutes();
	}
	
	@Transactional
	public void updateRouteRule(RoutingRule routingRule) {
		validateRoutingRule(routingRule);
		
		if (retrieveRouteRule (routingRule.getId()) == null) {
			throw new BadRequestException(String.format("Specified 'id': %s does not Exist", routingRule.getId()));
		}
		
		try {
			routingRuleMapper.updateRouteRule(routingRule);
		} catch (Exception e) {
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				throw new BadRequestException(String.format("Specified 'targetServiceId': %s does not Exist", routingRule.getTargetServiceId()));
			}
		}
		
		propagateService.propagateRoutes();
	}
	
	@Transactional
	public void deleteRouteRule(RoutingRule routingRule) {
		if (retrieveRouteRule (routingRule.getId()) == null) {
			throw new BadRequestException(String.format("Specified 'id': %s does not Exist", routingRule.getId()));
		}
		
		routingRuleMapper.deleteRouteRule(routingRule);
		propagateService.propagateRoutes();
	}
	
	public void refreshRouteRule() {
		routingRuleEventPublisher.publish(new RoutingRuleEvent(EventType.COMPLETE_UPDATED, null));
	}

	public void propagateRouteRule() {
		propagateService.propagateRoutes();
	}
	
	private void validateRoutingRule (RoutingRule routingRule) {
		validateMandatoryFields(routingRule);
	}
	
	private void validateMandatoryFields (RoutingRule routingRule) {
		if (routingRule.getId() == null) {
			throw new BadRequestException("'id' must be specified");
		}

		if (routingRule.getTargetServiceId() == null) {
			throw new BadRequestException("'targetServiceId' must be specified");
		}
	}
}
