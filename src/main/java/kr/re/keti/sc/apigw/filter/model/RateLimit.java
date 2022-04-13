package kr.re.keti.sc.apigw.filter.model;

import java.util.HashMap;
import java.util.Map;

public class RateLimit {
	
	public static enum Type {
		ClientId ("clientId"),
		Ip ("ip"),
		HostName ("hostName"),
		;
		
		private final String typeString;
		
		Type (String type) {
			this.typeString = type;
		}
		
		private static final Map<String, Type> valueMap = new HashMap<>(Type.values().length);
		
		static {
			for (Type it : values()) {
				valueMap.put(it.getTypeString(), it);
			}
		}

		public String getTypeString() {
			return this.typeString;
		}

		public static Type fromTypeString(String typeString) {
			return valueMap.get(typeString);
		}
	}
	
	public static enum Scope {
		Local ("local"),
		Cluster ("cluster"),
		;
		
		private final String scopeString;
		
		Scope (String scopeString) {
			this.scopeString = scopeString;
		}
		
		private static final Map<String, Scope> valueMap = new HashMap<>(Scope.values().length);
		
		static {
			for (Scope it : values()) {
				valueMap.put(it.getScopeString(), it);
			}
		}

		public String getScopeString() {
			return this.scopeString;
		}

		public static Scope fromScopeString(String scopeString) {
			return valueMap.get(scopeString);
		}
	}
		
}
