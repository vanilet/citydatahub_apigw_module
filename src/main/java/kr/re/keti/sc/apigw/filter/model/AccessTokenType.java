package kr.re.keti.sc.apigw.filter.model;

import java.util.HashMap;
import java.util.Map;

public enum AccessTokenType {
		Bearer ("bearer"),
		;
		
		private final String accessTokenTypeString;
		
		AccessTokenType (String accessTokenTypeString) {
			this.accessTokenTypeString = accessTokenTypeString;
		}
		
		private static final Map<String, AccessTokenType> valueMap = new HashMap<>(AccessTokenType.values().length);
		
		static {
			for (AccessTokenType it : values()) {
				valueMap.put(it.getAccessTokenTypeString(), it);
			}
		}

		public String getAccessTokenTypeString() {
			return this.accessTokenTypeString;
		}

		public static AccessTokenType fromAccessTokenTypeString(String accessTokenTypeString) {
			return valueMap.get(accessTokenTypeString);
		}
}
