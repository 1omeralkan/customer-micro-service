package com.omeralkan.customer.config;

import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (operation, handlerMethod) -> {

            operation.addParametersItem(new HeaderParameter()
                    .name("Accept-Language")
                    .description("Dil Seçeneği (İngilizce için 'en', Türkçe için boş bırakın veya 'tr' yazın)")
                    .required(false)); // Zorunlu değil (false). Gönderilmezse sistem 'tr' kabul edecek.

            return operation;
        };
    }
}