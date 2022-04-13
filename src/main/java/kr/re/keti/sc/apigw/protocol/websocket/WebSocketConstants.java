package kr.re.keti.sc.apigw.protocol.websocket;

import org.springframework.stereotype.Component;

@Component
public class WebSocketConstants {
	public static final String AUTHORIZATION_QUERYSTRING_FIELD_NAME = "token";
	
	public static final Integer PATH_INDEX = 1;

	public static String CONTEXT_PATH = "/websocket";
}
