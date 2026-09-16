package com.kronos.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditJacksonConfig {

    @Bean
    public ObjectMapper auditObjectMapper() {
        Hibernate6Module hibernateModule = new Hibernate6Module();
        hibernateModule.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(hibernateModule);

        return mapper;
    }
}
