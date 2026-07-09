package com.smarthire.auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.server.url}")
    private String gatewayUrl;

    @Bean
    public OpenAPI customOpenAPI() {

        Server server = new Server();
        server.setUrl(gatewayUrl);
        server.setDescription("SmartHire API Gateway");

        return new OpenAPI()

                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0"))

                .servers(List.of(server));
    }
}