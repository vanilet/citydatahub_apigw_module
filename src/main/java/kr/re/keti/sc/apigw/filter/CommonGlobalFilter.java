package kr.re.keti.sc.apigw.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.re.keti.sc.apigw.filter.model.AccessTokenInfo;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class CommonGlobalFilter {
	
	@Bean
	public GlobalFilter loggingFilter() {
	    return (exchange, chain) -> {
	    	Set<URI> uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
	        String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
	        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
	        URI routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
	        log.info("Incoming request {} is routed to id: {}, uri: {}, Request Info: IP: {}, Port: {}, Aud: {}, UserID: {}, Method: {}, URI: {}, Headers: {}, QueryParameters: {}",
	        		originalUri,
	        		route.getId(),
	        		routeUri,
	        		exchange.getRequest().getRemoteAddress().getAddress(),
	        		exchange.getRequest().getRemoteAddress().getPort(),
	        		exchange.getAttributes().get(AccessTokenInfo.Payload.AUD) != null ? exchange.getAttributes().get(AccessTokenInfo.Payload.AUD) : "Unknown",
	        		exchange.getAttributes().get(AccessTokenInfo.Payload.USER_ID) != null ? exchange.getAttributes().get(AccessTokenInfo.Payload.USER_ID) : "Unkown", 
	    	    	exchange.getRequest().getMethod(), 
	    	    	exchange.getRequest().getPath(), 
	    	    	exchange.getRequest().getHeaders(), 
	    	    	exchange.getRequest().getQueryParams().toString());
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
	    		log.info("Response Info - RSC: {}", exchange.getResponse().getStatusCode());
	        }));
	    };
	}
}