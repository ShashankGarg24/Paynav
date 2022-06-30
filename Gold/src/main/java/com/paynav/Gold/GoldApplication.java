package com.paynav.Gold;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class GoldApplication {

	@Bean
	public WebClient getWebClientBuilder(){
		return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodecs).build())
				.build();
	}

	private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
		clientCodecConfigurer.defaultCodecs().maxInMemorySize(16*1024*1024);
		clientCodecConfigurer.customCodecs().registerWithDefaultConfig(new Jackson2JsonDecoder(new ObjectMapper(), MediaType.TEXT_HTML));
		clientCodecConfigurer.customCodecs().registerWithDefaultConfig(new Jackson2JsonEncoder(new ObjectMapper(), MediaType.TEXT_HTML));
	}

	@Bean
	public Docket swaggerConfig() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.paths(PathSelectors.ant("/api/**"))
				.apis(RequestHandlerSelectors.basePackage("com.paynav.Gold"))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GoldApplication.class, args);
	}

}
