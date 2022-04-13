package kr.re.keti.sc.apigw.routingrule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.re.keti.sc.apigw.routingrule.model.RoutingRuleApplicationEvent;

@Component
public class RoutingRuleEventPublisher {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public void publish(final RoutingRuleApplicationEvent.RoutingRuleEvent routingRuleEvent) {
		RoutingRuleApplicationEvent routingRuleApplicationEvent = new RoutingRuleApplicationEvent(this, routingRuleEvent);
		applicationEventPublisher.publishEvent(routingRuleApplicationEvent);
	}
}
