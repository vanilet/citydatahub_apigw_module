package kr.re.keti.sc.apigw.routingrule.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoutingRuleDetailType {
	Path ("PATH", "000"), 
	Header ("HEADER", "001"),
	QueryString ("QUERYSTRING", "002"),
	;
	
	private final String value;
	private final String code;
	
	RoutingRuleDetailType (String value, String code) {
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

	private static final Map<String, RoutingRuleDetailType> valueMap = new HashMap<>(RoutingRuleDetailType.values().length);
	private static final Map<String, RoutingRuleDetailType> codeMap = new HashMap<>(RoutingRuleDetailType.values().length);
	
	static {
		for (RoutingRuleDetailType it : values()) {
			valueMap.put(it.getValue(), it);
			codeMap.put(it.getCode(), it);
		}
	}

	public static RoutingRuleDetailType parseCodeString(String code) {
		if (codeMap.get(code) != null) {
			return codeMap.get(code);
		}
		throw new IllegalArgumentException("No matching constant for [" + code + "]");
	}
	
	@JsonCreator
	public static RoutingRuleDetailType parseValueString(String value) {
		if (value != null && valueMap.get(value.toUpperCase()) != null) {
			return valueMap.get(value.toUpperCase());
		}
		throw new IllegalArgumentException("No matching constant for [" + value + "]");
	}
}
