package com.nimdec.api.config;

import com.nimdec.api.annotation.Trophy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonBeans {

    @Trophy("realLife")
    public String specialBean() {
        return "Walking trophy";
    }
}
