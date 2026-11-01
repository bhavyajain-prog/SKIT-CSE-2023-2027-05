package com.kronos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * AuditJacksonConfig declares its own ObjectMapper bean, which makes Spring Boot skip its
 * default one (the one that can write java.time.Instant). This brings back a Boot-configured
 * mapper as the primary one for HTTP request/response JSON. The audit mapper is still
 * looked up by name ("auditObjectMapper") and is not affected.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build();
    }
}
