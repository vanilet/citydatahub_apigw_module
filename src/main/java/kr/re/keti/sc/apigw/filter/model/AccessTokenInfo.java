package kr.re.keti.sc.apigw.filter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
public class AccessTokenInfo {
	AccessTokenType type;
	String value;
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Payload {
		public static final String AUD = "kr.re.keti.sc.apigw.filter.model.AccessTokenInfo.Payload.aud";
		public static final String USER_ID = "kr.re.keti.sc.apigw.filter.model.AccessTokenInfo.Payload.userId";
		String type;
		String userId;
		Long exp;
		String role;
		String aud;
		
		String rawPayload;
	}
}
