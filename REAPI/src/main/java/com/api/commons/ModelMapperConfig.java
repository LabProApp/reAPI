package com.api.commons;



import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class that exposes a fully configured
 * {@link ModelMapper} bean for use throughout the application.
 *
 * <p>The mapper is set up with the following options:</p>
 * <ul>
 *   <li><b>Field matching enabled</b> — mapping is performed at the field level,
 *       not via public getters/setters.</li>
 *   <li><b>Private field access</b> — private fields are accessible during
 *       mapping without requiring accessor methods.</li>
 *   <li><b>Skip nulls</b> — source fields with a {@code null} value are ignored,
 *       leaving the corresponding destination field unchanged.</li>
 *   <li><b>STRICT matching strategy</b> — source and destination property names
 *       must match exactly, preventing accidental cross-field mappings.</li>
 * </ul>
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and configures the application-wide {@link ModelMapper} instance.
     *
     * @return a {@link ModelMapper} configured with strict, field-level,
     *         null-skipping mapping settings
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }
}
