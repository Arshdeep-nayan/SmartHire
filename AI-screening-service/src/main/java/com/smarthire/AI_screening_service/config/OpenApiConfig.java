package com.smarthire.AI_screening_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

        final String securitySchemeName = "Bearer Authentication";

        Server server = new Server();
        server.setUrl(gatewayUrl);
        server.setDescription("SmartHire API Gateway");

        return new OpenAPI()

                .info(new Info()
                        .title("AI Screening Service API")
                        .version("1.0")
                        .description("""
                                ## Before Testing Protected APIs

                                1. Select **Authentication Service** from the service dropdown.

                                2. Login using **POST /api/login**.

                                3. Copy the JWT token from the response.

                                4. Switch back to the AI Screening Service from the service dropdown.

                                5. Click the **Authorize** button.

                                6. Paste the JWT token into the authorization field.

                                7. Click **Authorize**.

                                8. You can now test any protected API endpoint.
                                """))

                .servers(List.of(server))

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}