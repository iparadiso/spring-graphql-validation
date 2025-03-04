package com.example.demo;

import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLValidationConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> {

            // Create ValidationRules with specific directives
            ValidationRules rules = ValidationRules.newValidationRules()
                    .build();

            ValidationSchemaWiring validationSchemaWiring = new ValidationSchemaWiring(rules);
            builder.directiveWiring(validationSchemaWiring);
        };
    }
}