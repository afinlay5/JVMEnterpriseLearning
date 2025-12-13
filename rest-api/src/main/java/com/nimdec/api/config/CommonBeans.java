package com.nimdec.api.config;

import com.nimdec.api.annotation.Trophy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

@Configuration
public class CommonBeans {

    @Trophy("realLife")
    public String specialBean() {
        return "Walking trophy";
    }

    @Bean
    @Scope("prototype")
    public Temporal newBeanEveryTime() {
        return LocalDateTime.now();
    }
}

// black mirror episodes with programming