package kr.re.keti.sc.apigw.routingrule.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LogicalOperation {
	AND ("AND", "000"), 
	OR ("OR", "001"),
	;
	
	private final String value;
	private final String code;
	
	LogicalOperation (String value, String code) {
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
	
	private static final Map<String, LogicalOperation> valueMap = new HashMap<>(LogicalOperation.values().length);
	private static final Map<String, LogicalOperation> codeMap = new HashMap<>(LogicalOperation.values().length);
	
	static {
		for (LogicalOperation it : values()) {
			valueMap.put(it.getValue(), it);
			codeMap.put(it.getCode(), it);
		}
	}

	public static LogicalOperation parseCodeString(String code) {
		if (codeMap.get(code) != null) {
			return codeMap.get(code);
		}
		throw new IllegalArgumentException("No matching constant for [" + code + "]");
	}
	
	@JsonCreator
	public static LogicalOperation parseValueString(String value) {
		if (value != null && valueMap.get(value.toUpperCase()) != null) {
			return valueMap.get(value.toUpperCase());
		}
		throw new IllegalArgumentException("No matching constant for [" + value + "]");
	}
}
