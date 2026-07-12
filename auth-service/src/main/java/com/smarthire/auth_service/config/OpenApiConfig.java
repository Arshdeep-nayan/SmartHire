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
                        .title("Authentication Service API")
                        .version("1.0")
                        .description("""
                                ## Getting Started

                                This service is used to generate the JWT required for all protected APIs.

                                ### Steps

                                1. Register a new user using **POST /api/register** (if you don't already have an account).

                                2. Login using **POST /api/login**.

                                3. Copy the JWT token from the response.

                                4. Switch to the desired microservice from the service dropdown.

                                5. Click the **Authorize** button.

                                6. Paste the JWT token into the authorization field.

                                7. Click **Authorize**.

                                8. You can now test any protected API endpoint.
                                """))

                .servers(List.of(server));
    }
}