package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "app",
        "generator",
        "runtime",
        "simulator"
})
public class PlcSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlcSimulatorApplication.class, args);
    }
}
