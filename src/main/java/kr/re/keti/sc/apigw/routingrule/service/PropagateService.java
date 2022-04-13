package kr.re.keti.sc.apigw.routingrule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class PropagateService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${propagate.serviceId}")
    private String PROPAGATE_SERVICE; // APIGW Service Id

    @Value("${propagate.refresh.path}")
    private String REFRESH_PATH;     // refresh path

    @Value("${propagate.request.timeout}")
    private int TIMEOUT;

    @Value("${propagate.request.retry.Count}")
    private int RETRY;
    
    @Autowired
    private DiscoveryClient eureka;


    public void propagateRoutes() {
        eureka.getInstances(PROPAGATE_SERVICE).parallelStream().forEach(serviceInstance -> {
            Flux<String> propagateResult = initClient(serviceInstance);
            propagate(propagateResult, serviceInstance);
        });
    }

    public Flux<String> initClient(ServiceInstance serviceInstance){
    	
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceInstance.getUri().toString());
    	
    	builder.port(serviceInstance.getMetadata().get("management-port"));
    	builder.path(serviceInstance.getMetadata().get("management-context-path"));
    	
    	logger.debug("Propagation to "+builder.toUriString());

        WebClient webClient = WebClient.builder()
                .baseUrl(builder.toUriString())
                .build();

        return webClient.post().uri(REFRESH_PATH)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    logger.error("Error while calling endpoint {} with status code {}",
                            serviceInstance.getUri().toString(), clientResponse.statusCode());
                    throw new RuntimeException("Error while calling  accounts endpoint");
                })
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    logger.error("Error while calling endpoint {} with status code {}",
                            serviceInstance.getUri().toString(), clientResponse.statusCode());
                    throw new IllegalArgumentException("Error while calling  accounts endpoint");
                })
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(TIMEOUT)).retry(RETRY);
    }

    public void propagate(Flux<String> propagateResult, ServiceInstance serviceInstance) {
        propagateResult.doOnError(RuntimeException.class, e -> {
            logger.error( e.toString()+ "occured, to " + serviceInstance.getHost() +" propagate is failed");
        }).doOnComplete(() -> {
            logger.info("to " + serviceInstance.getHost() + " propagate complete");
        }).subscribe();
    }
}
