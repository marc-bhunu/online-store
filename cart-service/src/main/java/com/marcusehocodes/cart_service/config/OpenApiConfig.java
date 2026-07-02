package com.marcusehocodes.cart_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI cartServiceApiDocs(){
        return new OpenAPI().info(
                new Info()
                        .title("Cart Service API")
                        .description("Cart Service API for the online store API")
                        .contact(getContact())
                        .license(getLicense())
                        .version("1.0.0")
        );
    }

    private Contact getContact(){
        Contact contact = new Contact();
        contact.setEmail("email@user.com");
        contact.setName("Mark B.");
        contact.setUrl("https://contact-mark-b.user.com");
        return  contact;
    }

    private License getLicense(){
        License license = new License();
        license.setName("MIT");
        license.setUrl("https://access-lisence.com");
        return license;
    }
}
