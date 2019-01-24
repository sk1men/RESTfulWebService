package items.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Starts web server.
 */
@SpringBootApplication
@ComponentScan(basePackages = "items")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

