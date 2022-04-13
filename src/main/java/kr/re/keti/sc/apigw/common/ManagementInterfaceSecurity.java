package kr.re.keti.sc.apigw.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.re.keti.sc.apigw.protocol.http.AccessTokenHttpUtils;

@Component
public class ManagementInterfaceSecurity {
	
	@Value("${managementInterface.security.enabled}")
	private Boolean securityEnabled;
	
	@Value("#{'${managementInterface.security.allowed-access-token-roles}'.split(',')}")
	private List<String> allowedAccessControlRoles;
	
	@Autowired
	private AccessTokenVerifier accessTokenVerifier;
	
	public void apply(String accessTokenString) {
		if (securityEnabled != null && securityEnabled) {
			String accessTokenValue = AccessTokenHttpUtils.extractAccessToken(accessTokenString);
			accessTokenVerifier.verify(accessTokenValue, allowedAccessControlRoles);
		}
		
	}
}
