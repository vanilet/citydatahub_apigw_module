package kr.re.keti.sc.apigw.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.re.keti.sc.apigw.filter.HeaderUtils;
import reactor.core.publisher.Mono;

@Configuration
public class RedisRateLimitConfiguration {

    @Value("${clientRateLimit.burstCapacity}")
    private int burstCapacity;

    @Autowired
    private HeaderUtils headerUtils;

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(burstCapacity,burstCapacity);
    }

    @Bean
    public KeyResolver rateKeyResolver() {
        return exchange ->  Mono.just(headerUtils.getKey(exchange));
    }
}
