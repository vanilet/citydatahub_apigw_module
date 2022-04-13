package kr.re.keti.sc.apigw.common;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends
    AbstractErrorWebExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
 
    // constructors
 	public GlobalErrorWebExceptionHandler(GlobalErrorAttributes g, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
		super(g, new ResourceProperties(), applicationContext);
		super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
		// TODO Auto-generated constructor stub
	}

	@Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) { 
		//TODO: UI 또는 Spring Cloud Gateway Exception 이외에도 영향이 있는지 확인 필요
        return RouterFunctions.route(
          RequestPredicates.all(), this::renderErrorResponse);
    }
 
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
 
       Map<String, Object> errorPropertiesMap = getErrorAttributes(request, false);
       
       logger.debug("error occurs: " + errorPropertiesMap);
 
       Response response = (Response) errorPropertiesMap.get("response");
       
       return ServerResponse.status(response.getType().getHttpStatusCode())
         .contentType(MediaType.APPLICATION_JSON_UTF8)
         .body(BodyInserters.fromObject(response));
    }
}