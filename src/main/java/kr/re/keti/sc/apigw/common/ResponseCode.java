package kr.re.keti.sc.apigw.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseCode {

	OK ("http://citydatahub.kr/successes/OK", "OK", HttpStatus.OK),
	CREATED ("http://citydatahub.kr/successes/Created", "Created", HttpStatus.CREATED),
	DELETED ("http://citydatahub.kr/successes/Deleted", "OK", HttpStatus.OK),
	CHANGE ("http://citydatahub.kr/successes/Changed", "Changed", HttpStatus.OK),
	BAD_REQUEST  ("http://citydatahub.kr/errors/BadRequestData", "Bad Request", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED  ("http://citydatahub.kr/errors/Unauthorized", "Unauthorized", HttpStatus.UNAUTHORIZED),
	NOT_FOUND  ("http://citydatahub.kr/errors/ResourceNotFound", "Not Found", HttpStatus.NOT_FOUND),
	METHOD_NOT_ALLOWED  ("http://citydatahub.kr/errors/MethodNotAllowed", "Method Not Allowed", HttpStatus.METHOD_NOT_ALLOWED),
	NOT_ACCEPTABLE  ("http://citydatahub.kr/errors/UnsupportedMediaType", "Not Acceptable", HttpStatus.NOT_ACCEPTABLE),
	UNSUPPORTED_MEDIA_TYPE  ("http://citydatahub.kr/errors/UnsupportedMediaType", "Unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
	ALEADY_EXISTS  ("http://citydatahub.kr/errors/AlreadyExists", "Already Exists", HttpStatus.CONFLICT),
	MANDATORY_PARAMETER_MISSING  ("http://citydatahub.kr/errors/BadRequestData", "Mandatory Parameter Missing", HttpStatus.BAD_REQUEST),
	INVAILD_PARAMETER_TYPE  ("http://citydatahub.kr/errors/BadRequestData", "Invaild Parameter Type", HttpStatus.BAD_REQUEST),
	TOO_MANY_REQUETS  ("http://citydatahub.kr/errors/TooManyRequests", "Too Many Requests", HttpStatus.TOO_MANY_REQUESTS),
	INTERNAL_SERVER_ERROR  ("http://citydatahub.kr/errors/InternalError", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
	SERVICE_UNAVAILABLE  ("http://citydatahub.kr/errors/ServiceUnavailable", "Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE),
	GATEWAY_TIMEOUT  ("http://citydatahub.kr/errors/GatewayTimeout", "Gateway Timeout", HttpStatus.GATEWAY_TIMEOUT),
	NOT_REACHABLE  ("http://citydatahub.kr/errors/ServiceUnavailable", "Not Reachable", HttpStatus.SERVICE_UNAVAILABLE),
	;

	private final String detailResponseCode;
	private final String detailDescription;
	private final HttpStatus httpStatusCode;
	
	private ResponseCode(String detailResponseCode, String detailDescription,  HttpStatus httpStatusCode ) {
		this.detailResponseCode = detailResponseCode;
		this.detailDescription = detailDescription;
		this.httpStatusCode = httpStatusCode;
	}
	
	private static final Map<String, ResponseCode> valueMap = new HashMap<>(ResponseCode.values().length);
	
	static {
		for (ResponseCode it : values()) {
			valueMap.put(it.getDetailCode(), it);
		}
	}

	@JsonValue
	public String getDetailCode() {
		return this.detailResponseCode;
	}

	public String getDetailDescription() {
		return detailDescription;
	}
	
	public HttpStatus getHttpStatusCode() {
		return httpStatusCode;
	}
	
	@JsonCreator
	public static ResponseCode fromDetailResponseCode(String detailResponseCode) {
		return valueMap.get(detailResponseCode);
	}
}