package kr.re.keti.sc.apigw.routingrule.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceCommunicationMethod {
	Loadbalanced ("LB", "000"), 
	Http ("HTTP", "001"),
	Https ("HTTPS", "002"),
	WebSocket ("WS", "003"),
	WebSocketSSL ("WWS", "004"),
	;
	
	private final String value;
	private final String code;
	
	ServiceCommunicationMethod (String value, String code) {
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
	
	private static final Map<String, ServiceCommunicationMethod> valueMap = new HashMap<>(ServiceCommunicationMethod.values().length);
	private static final Map<String, ServiceCommunicationMethod> codeMap = new HashMap<>(ServiceCommunicationMethod.values().length);
	
	static {
		for (ServiceCommunicationMethod it : values()) {
			valueMap.put(it.getValue(), it);
			codeMap.put(it.getCode(), it);
		}
	}

	public static ServiceCommunicationMethod parseCodeString(String code) {
		if (codeMap.get(code) != null) {
			return codeMap.get(code);
		}
		throw new IllegalArgumentException("No matching constant for [" + code + "]");
	}
	
	@JsonCreator
	public static ServiceCommunicationMethod parseValueString(String value) {
		if (value != null && valueMap.get(value.toUpperCase()) != null) {
			return valueMap.get(value.toUpperCase());
		}
		throw new IllegalArgumentException("No matching constant for [" + value + "]");
	}
}
