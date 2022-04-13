package kr.re.keti.sc.apigw.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
public class TemplateConfiguration {

    private final int CONNECTION_TIMEOUT = 3;
    private final int READ_TIMEOUT = 3;

    @LoadBalanced
    @Bean
    public RestTemplate loadBalanced(){

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT))
                .setReadTimeout(Duration.ofSeconds(READ_TIMEOUT))
                .build();
    }

    @Primary
    @Bean
    public RestTemplate restTemplate() {

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT))
                .setReadTimeout(Duration.ofSeconds(READ_TIMEOUT))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}

