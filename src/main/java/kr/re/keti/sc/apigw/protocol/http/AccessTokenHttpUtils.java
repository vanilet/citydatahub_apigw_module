package kr.re.keti.sc.apigw.protocol.http;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import kr.re.keti.sc.apigw.common.exception.UnauthorizedException;
import kr.re.keti.sc.apigw.filter.model.AccessTokenType;
import kr.re.keti.sc.apigw.protocol.websocket.WebSocketConstants;

public class AccessTokenHttpUtils {
	private static final Integer ACCESS_TOKEN_COMPONENT_NUMBER = 2;
	private static final Integer ACCESS_TOKEN_TYPE_INDEX = 0;
	private static final Integer ACCESS_TOKEN_VALUE_INDEX = 1;
	
	public static String extractAccessToken(String authorizationString){
		
		if (authorizationString == null || authorizationString.isEmpty()) {
            throw new UnauthorizedException("Access Token Verification Failed : Access Token is empty");
        }
		
    	List <String> accessTokenElements = Arrays.asList(authorizationString.split("\\s"));
    	
		if(accessTokenElements.size() != ACCESS_TOKEN_COMPONENT_NUMBER) {
			throw new UnauthorizedException("Access Token Verification Failed : Malformat Authorization Content");
		}

		if(AccessTokenType.fromAccessTokenTypeString(accessTokenElements.get(ACCESS_TOKEN_TYPE_INDEX).trim().toLowerCase()) == null) {
			throw new UnauthorizedException("Access Token Verification Failed : Unsupported Authorization Type");
		}

		return accessTokenElements.get(ACCESS_TOKEN_VALUE_INDEX).trim();
    }
	
	public static String extractAccessToken(ServerHttpRequest request){
    	List <String> accessTokenElements = Arrays.asList(obtainAccessTokenValue(request).split("\\s"));
    	
		if(accessTokenElements.size() != ACCESS_TOKEN_COMPONENT_NUMBER) {
			throw new UnauthorizedException("Access Token Verification Failed : Malformat Authorization Content");
		}

		if(AccessTokenType.fromAccessTokenTypeString(accessTokenElements.get(ACCESS_TOKEN_TYPE_INDEX).trim().toLowerCase()) == null) {
			throw new UnauthorizedException("Access Token Verification Failed : Unsupported Authorization Type");
		}

		return accessTokenElements.get(ACCESS_TOKEN_VALUE_INDEX).trim();
    }

	public static  void addAccessTokenHeader (ServerWebExchange exchange, String accessTokenPayloadString) {
    	ServerHttpRequest request = exchange.getRequest().mutate().headers((httpHeaders) -> {
    		httpHeaders.set(HttpConstants.HeaderFieldName.DH_ACCESSTOKEN, accessTokenPayloadString);
    	}).build();
    	exchange.mutate().request(request).build();
    }
    
	private static  String obtainAccessTokenValue(ServerHttpRequest request){
		List <String> accessTokenValueList = null;
		if(isWebSocketRequest(request)) {//WebSocket API 일 경우
			MultiValueMap<String, String> queryParams = request.getQueryParams();

			if(!queryParams.containsKey(WebSocketConstants.AUTHORIZATION_QUERYSTRING_FIELD_NAME)) {//?token=Bearer ...
				throw new UnauthorizedException("Access Token Verification Failed : Authorization Query Parameter is missing");
			}

			accessTokenValueList = queryParams.get(WebSocketConstants.AUTHORIZATION_QUERYSTRING_FIELD_NAME);
		} else {//REST API 일 경우
			if (!request.getHeaders().containsKey(HttpConstants.HeaderFieldName.AUTHORIZATION)) {
	            throw new UnauthorizedException("Access Token Verification Failed : Authorization header is missing");
	        }

			accessTokenValueList = request.getHeaders().get(HttpConstants.HeaderFieldName.AUTHORIZATION);
		}
		
		if (accessTokenValueList == null || accessTokenValueList.isEmpty()) {
            throw new UnauthorizedException("Access Token Verification Failed : Access Token is empty");
        }
		
		//1개 이상의 Authorization Header가 전송되는 것을 고려하고 있지 않아 0번째를 획득 함
		return accessTokenValueList.get(0).trim();
	}
    
    private static Boolean isWebSocketRequest(ServerHttpRequest request){
		if (request.getPath() == null || request.getPath().subPath(WebSocketConstants.PATH_INDEX) == null) {
			return false;
		}
		
		if(WebSocketConstants.CONTEXT_PATH.equals(request.getPath().subPath(WebSocketConstants.PATH_INDEX).value())) {
			return true;
		}
		return false;
	}
}
