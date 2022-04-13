package kr.re.keti.sc.apigw.common;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PayloadTransformer;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import kr.re.keti.sc.apigw.common.exception.UnauthorizedException;
import kr.re.keti.sc.apigw.filter.model.AccessTokenInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccessTokenVerifier implements PayloadTransformer <AccessTokenInfo.Payload> {
	
	private static final String PUBLUCKEY_KEY = "publickey";

	@Value("${security.accessToken.publicKeyRetrievalUri}")
	private String publicKeyRetrievalUrl;
	
	@Value("${security.accessToken.defaultPublicKey}")
    private String defaultPublicKeyString;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private JWSVerifier verifier;
	
	@PostConstruct
    public void init() {
		String publicKeyString = obtainPublicKey();

		String publicKeyStringContent = parsePublicKey(publicKeyString);
		
		X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStringContent));
		
    	RSAPublicKey pubKey = null;
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException ("Exception occurs when Initializing Authorization Token Verifier.", e);
		}
		
		verifier = new RSASSAVerifier(pubKey);
    }

	
	@SuppressWarnings("unchecked")
	private String obtainPublicKeyFromRemote() {
		Map<String,String> keyMap = null;
		try {
			String publicKeyJson = restTemplate.getForObject(publicKeyRetrievalUrl,String.class);
			keyMap = objectMapper.readValue(publicKeyJson, Map.class);
		} catch (RestClientException e) {
			log.warn("Unable to reach Remote Public Key URI. Default Public Key are going to be used.", e);
			return null;
		} catch (IOException e) {
			log.warn("Unable to parse Remote Public Key. Default Public Key are going to be used.", e);
			return null;
		}
		
		return keyMap.get(PUBLUCKEY_KEY);
	}
	
	private String obtainDefaultPublicKey() {
		return defaultPublicKeyString;
	}
	
	private String obtainPublicKey () {
		String publicKeyFromRemote = obtainPublicKeyFromRemote ();
		if (publicKeyFromRemote != null) {
			return publicKeyFromRemote;
		} else {
			return obtainDefaultPublicKey (); 
		}
	}
	
	public AccessTokenInfo.Payload verify (String accessTokenString, List<String> allowedAuthorizedRoles) throws UnauthorizedException {
		// 1. Decode
		JWSObject jwsObject = decode(accessTokenString);

		// 2. Verify
		//2.1. Verify Signature
		verifySignature (jwsObject);
		
		//2.2. Obtain Payload
		AccessTokenInfo.Payload accessTokenPayload = this.transform(jwsObject.getPayload());
		if (accessTokenPayload == null) {
			throw new UnauthorizedException("Access Token Verification Failed : Invaild Token Payload Format");
		}
		
		//2.3. Verify Expiration Time
		verifyExpirationTime (accessTokenPayload.getExp());

		//2.4. Verify Authorization Role
		verifyAuthorizationRole (accessTokenPayload.getRole(), allowedAuthorizedRoles);
		
		return accessTokenPayload;
	}
	
	private JWSObject decode (String accessToken) throws UnauthorizedException {
    	try {
    		return JWSObject.parse(accessToken);
		} catch (Exception e) {
			throw new UnauthorizedException("Access Token Verification Failed : Invaild Token Format");
		}
    }
	
	@Override
	public AccessTokenInfo.Payload transform(Payload payload) {
		try {
			AccessTokenInfo.Payload accessTokenPayload = objectMapper.readValue(payload.toString(), AccessTokenInfo.Payload.class);
			accessTokenPayload.setRawPayload(payload.toString());
			return accessTokenPayload;
		} catch (IOException e) {
			log.error(String.format("Exception Occurs while Parsing JwtPayload. JwtPayload: %s", payload.toString()), e);
			return null;
		}
	}
	
	private String parsePublicKey (String publicKeyString) {
		if (publicKeyString != null) {
			return publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "").replaceAll(" ", "");
		} else {
			throw new IllegalStateException ("Exception occurs when Parsing Public Key for Verifying Authorization Tokenr.");
		}
	}

	private void verifySignature (JWSObject jwsObject) throws UnauthorizedException {
    	try {
			if (jwsObject.verify(verifier) == false) {
				throw new UnauthorizedException("Access Token Verification Failed : Invaild Token Signature");
			}
		} catch (JOSEException e) {
			throw new UnauthorizedException("Access Token Verification Failed : Unable to verify Token Signature");
		}
    }
    
    private void verifyExpirationTime (Long expirationTime) throws UnauthorizedException {
    	if (expirationTime == null) {
    		throw new UnauthorizedException("Access Token Verification Failed : Expiration Time is not Specified");
    	}
    	
    	if (expirationTime * 1000 < new Date().getTime()) {
    		throw new UnauthorizedException("Access Token Verification Failed : Access Token has expired");
    	}
    }
    
    private void verifyAuthorizationRole (String accessTokenRolesString, List<String> allowedAccessRolesForRoute) throws UnauthorizedException {
    	if (allowedAccessRolesForRoute == null) {//Currently 'null' means that this route is allowed to all clients 
    		return;
    	} else {
    		if (accessTokenRolesString == null) {//Client doesn't have any authorization roles
    			throw new UnauthorizedException("Access Token Verification Failed : Insufficient Access Rights");
    		} else {
    			List<String> allowedAccessRolesForRequest = Arrays.asList(accessTokenRolesString.replaceAll("\\s", "").split(","));
    			
    			for (String allowedAccessRoleForRoute : allowedAccessRolesForRoute) {
    				for (String allowedAccessRoleForRequest : allowedAccessRolesForRequest) {
    					if (allowedAccessRoleForRoute.equals(allowedAccessRoleForRequest)) {
    						return;
    					}
    				}
    			}
    			throw new UnauthorizedException("Access Token Verification Failed : Insufficient Access Rights");
    		}
    	}
    	
    }
}
