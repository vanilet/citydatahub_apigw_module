package kr.re.keti.sc.apigw.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.re.keti.sc.apigw.filter.model.AccessTokenInfo;
import kr.re.keti.sc.apigw.filter.model.RateLimit;
import kr.re.keti.sc.apigw.protocol.http.HttpConstants;

import java.io.IOException;
import java.util.Map;

@Component
public class HeaderUtils {

    @Value("${clientRateLimit.type}")
    private String clientRateLimitTypeString;

    @Autowired
    private ObjectMapper mapper;

    public String getKey(ServerWebExchange exchange) {
        if(clientRateLimitTypeString == null) {
            throw new IllegalArgumentException();
        } else if(RateLimit.Type.ClientId.equals(RateLimit.Type.fromTypeString(clientRateLimitTypeString))) {
            return this.getClientId(exchange);
        } else if(RateLimit.Type.HostName.equals(RateLimit.Type.fromTypeString(clientRateLimitTypeString))) {
            return this.getHostname(exchange);
        } else if(RateLimit.Type.Ip.equals(RateLimit.Type.fromTypeString(clientRateLimitTypeString))) {
            return this.getIp(exchange);
        }
        return null;
    }

    public String getClientId(ServerWebExchange exchange) {
    	if (exchange.getAttribute(AccessTokenInfo.Payload.AUD) != null) {
    		return exchange.getAttribute(AccessTokenInfo.Payload.AUD);
    	}
    	if (exchange.getRequest().getHeaders().get(HttpConstants.HeaderFieldName.DH_ACCESSTOKEN) != null) {
    		String accessTokenKey = exchange.getRequest().getHeaders().get(HttpConstants.HeaderFieldName.DH_ACCESSTOKEN).get(0);
            try {
                Map<String,Object> map = mapper.readValue(accessTokenKey, Map.class);
                return map.get(AccessTokenInfo.Payload.AUD).toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
        return "";
    }

    public String getHostname(ServerWebExchange exchange) {
        return exchange.getRequest().getRemoteAddress().getHostName();
    }

    public String getIp(ServerWebExchange exchange) {
        return exchange.getRequest().getRemoteAddress().getAddress().toString();
    }
}
