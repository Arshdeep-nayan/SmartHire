package com.smarthire.Api_Gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()

                .info(new Info()
                        .title("SmartHire API Gateway")
                        .version("1.0")
                        .description("""
                                ## 🚀 Welcome to SmartHire

                                SmartHire is a backend-only Job Portal built using Java Spring Boot Microservices.

                                ---
                                ## 🔑 Before Testing Protected APIs

                                1. Select **🔑 Auth Service (Login First)** from the service dropdown.
                                2. Use **POST /api/login** to generate a JWT.
                                3. Copy the JWT from the response.
                                4. Click the **Authorize (🔒)** button at the top of Swagger UI.
                                5. Paste the JWT into the authorization field and click **Authorize**.
                                6. Switch to any service and test the protected endpoints.

                                ---
                                ### 🌐 Public Endpoints

                                - POST `/api/register`
                                - POST `/api/login`

                                ---
                                ### 🔒 Authentication

                                All other endpoints require JWT authentication.

                                ---
                                ### 💻 Live Deployment

                                API Gateway:
                                http://13.62.16.155:8085
                                """)
                        .license(new License()
                                .name("MIT License")))

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