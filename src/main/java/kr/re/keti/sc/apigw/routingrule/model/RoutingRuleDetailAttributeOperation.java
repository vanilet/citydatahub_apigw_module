package kr.re.keti.sc.apigw.routingrule.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoutingRuleDetailAttributeOperation {
	Equal ("EQUAL", "000"), 
	NotEqual ("NOT_EQUAL", "001"),
	;
	
	private final String value;
	private final String code;
	
	RoutingRuleDetailAttributeOperation (String value, String code) {
		this.value = value;
		this.code = code;
	}
	
	@JsonValue
	public String getValue() {
		return value;
	}
	
	public String getCode() {
		return code;
	}
	
	private static final Map<String, RoutingRuleDetailAttributeOperation> valueMap = new HashMap<>(RoutingRuleDetailAttributeOperation.values().length);
	private static final Map<String, RoutingRuleDetailAttributeOperation> codeMap = new HashMap<>(RoutingRuleDetailAttributeOperation.values().length);
	
	static {
		for (RoutingRuleDetailAttributeOperation it : values()) {
			valueMap.put(it.getValue(), it);
			codeMap.put(it.getCode(), it);
		}
	}
	
	public static RoutingRuleDetailAttributeOperation parseCodeString(String code) {
		if (codeMap.get(code) != null) {
			return codeMap.get(code);
		}
		throw new IllegalArgumentException("No matching constant for [" + code + "]");
	}

	@JsonCreator
	public static RoutingRuleDetailAttributeOperation parseValueString(String value) {
		if (value != null && valueMap.get(value.toUpperCase()) != null) {
			return valueMap.get(value.toUpperCase());
		}
		throw new IllegalArgumentException("No matching constant for [" + value + "]");
	}
}
