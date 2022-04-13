package kr.re.keti.sc.apigw.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class Resilience4JConfiguration {
	
    private final Integer FAILURE_RATE_THRESHOLD = 20;
    private final Integer SLIDING_WINDOW_SIZE = 100;
    private final Integer MINIMUM_NUMBER_OF_CALLS = 1;
    private final SlidingWindowType SLIDING_WINDOW_TYPE = SlidingWindowType.TIME_BASED;
    private final Integer TIME_LIMITER_DURATION_SECONDS = 4;
    private final Integer WAIT_DURATION_IN_OPEN_STATE_SECONDS = 60;
    	
	@Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
				  .failureRateThreshold(FAILURE_RATE_THRESHOLD)
				  .slidingWindow(SLIDING_WINDOW_SIZE, MINIMUM_NUMBER_OF_CALLS, SLIDING_WINDOW_TYPE)
				  .waitDurationInOpenState(Duration.ofSeconds(WAIT_DURATION_IN_OPEN_STATE_SECONDS))
				  .build();
		
		TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
				.timeoutDuration(Duration.ofSeconds(TIME_LIMITER_DURATION_SECONDS))
				.build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
          .timeLimiterConfig(timeLimiterConfig)
          .circuitBreakerConfig(circuitBreakerConfig)
          .build());
    } 

}
