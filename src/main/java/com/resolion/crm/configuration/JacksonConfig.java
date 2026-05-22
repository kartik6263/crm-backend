package com.resolion.crm.configuration;


import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilderCustomizer;

@Configuration
public class JacksonConfig {

//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//
//        return builder ->
//                builder.featuresToEnable(
//                        MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS
//                );
//    }
@Bean
public ObjectMapper objectMapper() {

    ObjectMapper mapper = new ObjectMapper();

    mapper.configure(
            MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS,
            true
    );

    return mapper;
}
}
