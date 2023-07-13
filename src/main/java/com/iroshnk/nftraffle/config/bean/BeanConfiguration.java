package com.iroshnk.nftraffle.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(20000);
        factory.setReadTimeout(20000);
        return new RestTemplate(factory);
    }

//    @Bean
//    CustomPasswordEncoder passwordEncoder() {
//        //return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 14);
//        return new CustomPasswordEncoder();
//    }

    @Bean
    Argon2PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("lang/messages"); // Set the base name of your properties files
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
