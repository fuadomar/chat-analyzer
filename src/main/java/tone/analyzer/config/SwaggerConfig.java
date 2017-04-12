package tone.analyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tone.analyzer.controller.ToneAnalyzerController;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

/** Created by mozammal on 2/5/17. */
@Configuration
@EnableSwagger2
@ComponentScan(basePackageClasses = {ToneAnalyzerController.class})
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

  @Bean
  public UiConfiguration uiConfig() {
    return UiConfiguration.DEFAULT;
  }

  private ApiInfo metadata() {
    return new ApiInfoBuilder()
        .title("Biyeta rest API")
        .description("REST API of Biyeta")
        .version("1.0")
        .contact("mozamaml@example.com")
        .build();
  }
}
