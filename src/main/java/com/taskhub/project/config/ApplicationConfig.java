package com.taskhub.project.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Converter from List<String> to String
        Converter<List<String>, String> listToStringConverter = new AbstractConverter<>() {
            @Override
            protected String convert(List<String> source) {
                if (source == null) {
                    return null;
                }
                return String.join(",", source);
            }
        };

        // Converter from String to List<String>
        Converter<String, List<String>> stringToListConverter = new AbstractConverter<>() {
            @Override
            protected List<String> convert(String source) {
                if (source == null) {
                    return null;
                }
                return new ArrayList<>(List.of(source.split(",")));
            }
        };

        Converter<LocalDateTime, String> localDateTimeToStringConverter = new AbstractConverter<>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected String convert(LocalDateTime source) {
                if (source == null) {
                    return null;
                }
                return formatter.format(source);
            }
        };

        Converter<String, LocalDateTime> stringToLocalDateTimeConverter = new AbstractConverter<>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected LocalDateTime convert(String source) {
                if (source == null) {
                    return null;
                }
                return LocalDateTime.parse(source, formatter);
            }
        };

        // Add the converters to the model mapper
        modelMapper.addConverter(listToStringConverter);
        modelMapper.addConverter(stringToListConverter);
        modelMapper.addConverter(localDateTimeToStringConverter);
        modelMapper.addConverter(stringToLocalDateTimeConverter);

        return modelMapper;
    }
}

