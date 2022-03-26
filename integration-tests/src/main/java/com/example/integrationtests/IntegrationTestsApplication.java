package com.example.integrationtests;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntegrationTestsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationTestsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("User dir: " + System.getProperty("user.dir"));
    }
}
