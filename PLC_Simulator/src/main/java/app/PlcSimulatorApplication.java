package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
