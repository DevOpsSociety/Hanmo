package org.example.hanmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

// @OpenAPIDefinition(servers = {@Server(url = "https://hanmo.store", description = "Hanmo도메인")})
@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableCaching
public class HanmoApplication {
  public static void main(String[] args) {
    SpringApplication.run(HanmoApplication.class, args);
  }
}
