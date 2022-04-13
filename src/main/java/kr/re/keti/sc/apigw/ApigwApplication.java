package kr.re.keti.sc.apigw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan
public class ApigwApplication {
	
//	@Bean
//    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//                .authorizeExchange()
//                  .anyExchange()
//                    .authenticated()
//                .and()
//                  .oauth2Login();
////                .and()
////                  .oauth2ResourceServer()
////                    .jwt().jwtAuthenticationConverter(new JwtOAuth2AuthenticationTokenConverter());
//        return http.build();
//    }

	public static void main(String[] args) {
		SpringApplication.run(ApigwApplication.class, args);
	}
}