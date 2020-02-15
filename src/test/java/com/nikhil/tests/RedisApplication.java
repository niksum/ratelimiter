package com.nikhil.tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RedisApplication {

    public static void main(String... args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @RestController
    public class ServiceController {

        public static final String RESPONSE_BODY = "ResponseBody";

        @GetMapping("/api/v1/developers")
        public ResponseEntity<String> developers() {
        	System.out.println("In /api/v1/developers");
            return ResponseEntity.ok(RESPONSE_BODY);
        }

        @GetMapping("/api/v1/organizations")
        public ResponseEntity<String> organizations() {
        	System.out.println("In /api/v1/organizations");
            return ResponseEntity.ok(RESPONSE_BODY);
        }       

        @GetMapping("/api/v3/developers")
        public ResponseEntity<String> developersV3() {
        	System.out.println("/api/v3/developers");
            return ResponseEntity.ok(RESPONSE_BODY);
        }

    }
}
