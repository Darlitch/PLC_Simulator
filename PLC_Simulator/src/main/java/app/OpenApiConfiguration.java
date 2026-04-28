package app;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI plcSimulatorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PLC Simulator API")
                        .version("1.0")
                        .description("API for loading PoST models, generating runtime code, and controlling PLC simulations.")
                        .contact(new Contact().name("PLC Simulator"))
                        .license(new License().name("Internal Use")));
    }
}
