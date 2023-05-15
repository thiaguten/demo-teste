package com.example.demoteste.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Thiago Gutenberg C. da Costa
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                // Ignorar campos nulos globalmente
                .serializationInclusion(Include.NON_NULL)
                .modules(hibernate5Module(), javaTimeModule());
    }

    @Bean
    public com.fasterxml.jackson.databind.Module hibernate5Module() {
        // Ajuste para resolver o erro:
        // org.springframework.http.converter.HttpMessageNotWritableException: Could not
        // write JSON: failed to lazily initialize a collection of role...
        return new Hibernate5Module();
    }

    @Bean
    public com.fasterxml.jackson.databind.Module javaTimeModule() {
        // Ajuste para resolver o erro:
        // org.springframework.http.converter.HttpMessageConversionException: Type
        // definition error: [simple type, class java.time.LocalDateTime]; nested
        // exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
        // Java 8 date/time type `java.time.LocalDateTime` not supported by default: add
        // Module \"com.fasterxml.jackson.datatype:jackson-datatype-jsr310\" to enable
        // handling
        return new JavaTimeModule();
    }
}
