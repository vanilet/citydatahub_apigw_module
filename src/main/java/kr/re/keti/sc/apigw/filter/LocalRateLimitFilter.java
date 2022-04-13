package kr.re.keti.sc.apigw.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import kr.re.keti.sc.apigw.common.exception.TooManyRequestException;
import kr.re.keti.sc.apigw.filter.model.FilterOrderContstant;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LocalRateLimitFilter extends AbstractGatewayFilterFactory<LocalRateLimitFilter.Config> {

	private static final int EXPIRED_TIME = 1;
	private static final Long EXPIRED_REMAINING = 0l;
	public static final String REMAINING_HEADER = "X-RateLimit-Remaining";
	public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";
	public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

	@Autowired
	private HeaderUtils headerUtils;

	private Cache<String, List<String>> rateLimitCache;

	@Value("${clientRateLimit.burstCapacity}")
	private int burstCapacity;

	@Value("${clientRateLimit.type}")
	private String type;


	@PostConstruct
	public void init() {
		initCache();
	}

	public void initCache() {
		rateLimitCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRED_TIME,TimeUnit.SECONDS).build();
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter((exchange, chain) -> {
			log.debug("localRateLimitFilter called");

			//1. get ClientId / HostName / Ip
			String key = headerUtils.getKey(exchange);
			log.debug("localRateLimitFilter key:" + key);

			//2. Validate cache and if value exceed capacity size and then applying rateLimit
			isAllowed(key,exchange);
			
			Integer remainCapacity = burstCapacity - ((rateLimitCache.getIfPresent(key) == null) ? 0 : (rateLimitCache.getIfPresent(key).size()));
			
			exchange.getResponse().getHeaders().add(REMAINING_HEADER, remainCapacity.toString());
			exchange.getResponse().getHeaders().add(REPLENISH_RATE_HEADER, String.valueOf(burstCapacity));
			exchange.getResponse().getHeaders().add(BURST_CAPACITY_HEADER, String.valueOf(burstCapacity));

			return chain.filter(exchange);
			
		}, FilterOrderContstant.RATELIMITER);
	}

	@Component
	public static class Config {

	}

    private void isAllowed(String key, ServerWebExchange exchange) {
		log.debug("Whole RateLimit Cache Size : {}",rateLimitCache.size());

        if(rateLimitCache.asMap().containsKey(key)){
            List<String> valueCache = rateLimitCache.getIfPresent(key);
			isRateOver(valueCache,exchange);
			valueCache.add("");
        } else {
        	List<String> valueCache = new ArrayList<String>();
			valueCache.add("");
            rateLimitCache.put(key, valueCache);
        }
    }

    private void isRateOver(List <String> valueCache,ServerWebExchange exchange) {
        if(valueCache.size() >= burstCapacity) {
            //3-1. set X-RateLimit-Remaining, X-RateLimit-Burst-Capacity, X-RateLimit-Replenish-Rate in Header
            this.setHeader(exchange,EXPIRED_REMAINING);
            //3-2. set 429 TooManyRequestException
            StringBuilder sb = new StringBuilder();
            sb.append("Too many request");
            throw new TooManyRequestException(sb.toString());
        }
    }

	private void setHeader(ServerWebExchange exchange, Long tokensLeft) {
		exchange.getResponse().getHeaders().add(REMAINING_HEADER, tokensLeft.toString());
		exchange.getResponse().getHeaders().add(REPLENISH_RATE_HEADER, String.valueOf(burstCapacity));
		exchange.getResponse().getHeaders().add(BURST_CAPACITY_HEADER, String.valueOf(burstCapacity));
	}
}