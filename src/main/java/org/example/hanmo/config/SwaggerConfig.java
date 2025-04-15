package org.example.hanmo.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

@Configuration
public class SwaggerConfig {

  private SecurityScheme createTempTokenAuthScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.APIKEY)
        .in(SecurityScheme.In.HEADER)
        .name("tempToken");
  }

  private OpenApiCustomizer createTempOpenApiCustomizer(String title, String version) {
    return openApi -> {
      openApi.info(new Info().title(title).version(version));
      openApi.addSecurityItem(new SecurityRequirement().addList("tempTokenAuth"));
      openApi.schemaRequirement("tempTokenAuth", createTempTokenAuthScheme());
      // 서버 URL에 context-path(/api)를 포함하여 Swagger 문서에 반영
            openApi.setServers(List.of(new Server().url("https://hanmo.store/api")));
    };
  }

  @Bean
  public GroupedOpenApi allApi() {
    return GroupedOpenApi.builder()
        .group("All Users")
        .pathsToMatch("/**")
        .displayName("all API")
        .addOpenApiCustomizer(createTempOpenApiCustomizer("모든 API", "v0.4"))
        .build();
  }
}
