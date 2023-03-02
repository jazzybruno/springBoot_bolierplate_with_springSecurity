package rw.pacis_nkubito.boilerplateswagger.v1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerApiDoc extends WebMvcConfigurationSupport {

    private final ServletContext servletContext;

    @Autowired
    public SwaggerApiDoc(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Value("${server.host}")
    private String host;

    @Value("${swagger.app_name}")
    private String appName;

    @Value("${swagger.app_description}")
    private String appDescription;

    @Value("${swagger.end_point}")
    private String swaggerEndpoint;

    @Value("${swagger.base_controller_path}")
    private String baseControllerPath;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(swaggerEndpoint).addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).directModelSubstitute(LocalDate.class, Date.class)
                .pathProvider(new RelativePathProvider(servletContext) {
                }).select().apis(RequestHandlerSelectors.basePackage(baseControllerPath))
                .paths(PathSelectors.any()).build().apiInfo(apiInfo()).securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/.*")).build();
    }

    private List<SecurityReference> defaultAuth() {
        final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
        return Collections.singletonList(new SecurityReference("Bearer", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(appName).description(appDescription)
                .termsOfServiceUrl("http://www.rca.ac.rw/") .version("1.0").build();
    }
}
