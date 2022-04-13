package kr.re.keti.sc.apigw.common;

import java.net.ConnectException;
import java.util.Map;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.codec.CodecException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import kr.re.keti.sc.apigw.common.exception.BaseException;
import kr.re.keti.sc.apigw.common.exception.ResponseCodeType;
import kr.re.keti.sc.apigw.filter.model.AccessTokenInfo;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GlobalErrorAttributes extends DefaultErrorAttributes {
	
	private static final String responseString = "response";
	
	public GlobalErrorAttributes() {
        super(false); 
    }
	
	@Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
		Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);
		
		Throwable error = getError(request);
		
		if (error instanceof ResponseStatusException) {
			if (HttpStatus.NOT_FOUND.equals(((ResponseStatusException)error).getStatus())) {
				map.put(responseString, new Response (ResponseCode.NOT_FOUND, String.format("Target not found. Path: %s", map.get("path"))));
			} else if (HttpStatus.BAD_REQUEST.equals(((ResponseStatusException)error).getStatus())) {
				if (error.getCause() != null && error.getCause() instanceof CodecException) {//HTTP Body Unmarshalling Exception
					map.put(responseString, new Response (ResponseCode.BAD_REQUEST, error.getCause().getMessage()));
				} else {
					map.put(responseString, new Response (ResponseCode.BAD_REQUEST, map.get("message")));
				}
			} else if (HttpStatus.SERVICE_UNAVAILABLE.equals(((ResponseStatusException) error).getStatus())){
				map.put(responseString, new Response(ResponseCode.SERVICE_UNAVAILABLE, String.format("Service Unavailable. Cannot Route Path: %s", map.get("path"))));
			} else if (HttpStatus.GATEWAY_TIMEOUT.equals(((ResponseStatusException) error).getStatus())){
				map.put(responseString, new Response(ResponseCode.GATEWAY_TIMEOUT, map.get("message")));
			} else {
				//TODO: handle more error code
				map.put(responseString, new Response (ResponseCode.INTERNAL_SERVER_ERROR, map.get("message")));
			}
		} else if (error instanceof ConnectException) {
			map.put(responseString, new Response (ResponseCode.INTERNAL_SERVER_ERROR, "Connection Timed Out. Service Temporally Unavailable."));
		} else if (error instanceof NullPointerException) {
			map.put(responseString, new Response (ResponseCode.INTERNAL_SERVER_ERROR, String.format("No Response from Service. Route Path: %s", map.get("path"))));
		} else if (error instanceof BaseException) {
			ResponseCodeType responseCodeType = AnnotatedElementUtils.findMergedAnnotation(error.getClass(), ResponseCodeType.class);
			map.put(responseString, new Response (responseCodeType.value(), ((BaseException)error).getDetailDescription()));
		} else if (error instanceof CodecException) {
			if (error.getCause() != null && error.getCause() instanceof InvalidDefinitionException) {//Enumeration Exception
				map.put(responseString, new Response (ResponseCode.BAD_REQUEST, error.getCause().getMessage()));
			} else {
				map.put(responseString, new Response (ResponseCode.BAD_REQUEST, map.get("message")));
			}
		} else {
			map.put(responseString, new Response (ResponseCode.INTERNAL_SERVER_ERROR, map.get("message")));
		}
		
		log.info("Exception occurs on the Request: IP: {}, Port: {}, Aud: {}, UserID: {}, Method: {}, URI: {}, Headers: {}, QueryParameters: {}, Response: {}",
        		request.exchange().getRequest().getRemoteAddress().getAddress(),
        		request.exchange().getRequest().getRemoteAddress().getPort(),
        		request.exchange().getAttributes().get(AccessTokenInfo.Payload.AUD) != null ? request.exchange().getAttributes().get(AccessTokenInfo.Payload.AUD) : "Unknown",
        		request.exchange().getAttributes().get(AccessTokenInfo.Payload.USER_ID) != null ? request.exchange().getAttributes().get(AccessTokenInfo.Payload.USER_ID) : "Unkown", 
        		request.exchange().getRequest().getMethod(), 
        		request.exchange().getRequest().getPath(), 
        		request.exchange().getRequest().getHeaders(), 
        		request.exchange().getRequest().getQueryParams().toString(),
        		map.get(responseString)
        		);
		
        return map;
    }
}