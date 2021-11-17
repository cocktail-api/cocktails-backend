package de.slevermann.cocktails.backend.configuration.jdbi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jdbi.logging")
@Data
public class JdbiConfigurationProperties {
    private boolean enabled = false;
    private boolean showParameters = false;
}
