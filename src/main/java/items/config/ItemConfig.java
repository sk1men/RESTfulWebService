package items.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ItemConfig {

    @Value("${config.items.last.size:100}")
    private Integer size;

    @Value("${config.items.last.seconds:2}")
    private Integer seconds;

    public Integer getSize() {
        return size;
    }

    public Duration getDuration() {
        return Duration.ofSeconds(seconds);
    }
}
