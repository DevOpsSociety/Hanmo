package org.example.hanmo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "https://hanmo.store", description = "Hanmo도메인")})
@SpringBootApplication
public class HanmoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanmoApplication.class, args);
    }
}
