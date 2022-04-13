package kr.re.keti.sc.apigw.routingrule.model;

import org.springframework.context.ApplicationEvent;

import lombok.Data;

public class RoutingRuleApplicationEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 2103958203785239324L;
	
	private RoutingRuleEvent routingRuleEvent;

	public RoutingRuleApplicationEvent(final Object source, final RoutingRuleEvent routingRuleEvent) {
		super(source);
		this.routingRuleEvent = routingRuleEvent;
	}
	
	public RoutingRuleEvent getRoutingRuleEvent() {
		return routingRuleEvent;
	}

	public void setRoutingRuleEvent(RoutingRuleEvent routingRuleEvent) {
		this.routingRuleEvent = routingRuleEvent;
	}

	
	@Data public static class RoutingRuleEvent {
		private EventType eventType;
		private RoutingRule routingRule;
		
		public RoutingRuleEvent (EventType eventType, RoutingRule routingRule) {
			this.eventType = eventType;
			this.routingRule = routingRule;
		}

		public static enum EventType {
			CREATED, 
			COMPLETE_UPDATED,
			DELETED,
			;
		}
	}
}
