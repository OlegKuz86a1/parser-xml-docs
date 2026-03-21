package com.artwell;

import com.artwell.config.ArtwellXsdProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ArtwellXsdProperties.class)
public class ArtwellApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtwellApplication.class, args);
    }
}
