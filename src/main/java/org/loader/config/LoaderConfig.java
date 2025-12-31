package org.loader.config;

import org.loader.model.enums.Provider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for the Loader microservice.
 * Maps to 'loader.*' properties in application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "loader")
@Data
public class LoaderConfig {
    /**
     * Base directory where provider data files are stored
     * Default: /data
     */
    private String dataDirectory = "/data";

    /**
     * List of enabled providers
     * Default: github, jira, clickup
     */
    private List<String> providers = List.of("github", "jira", "clickup");

    /**
     * Whether scheduled scanning is enabled
     * Default: true
     */
    private boolean schedulerEnabled = true;

    /**
     * Cron expression for scheduled file scanning
     * Default: Every hour at minute 0 (0 0 * * * *)
     */
    private String schedulerCron = "0 0 * * * *";

    /**
     * Maximum number of records to process per file
     * Default: 10000 (to prevent memory issues)
     */
    private int maxRecordsPerFile = 10000;

    /**
     * Whether to skip files with parsing errors
     * If false, throws exception on parse error
     * Default: false
     */
    private boolean skipOnError = false;

    /**
     * Get full path to provider directory
     *
     * @param provider the provider (github, jira, clickup)
     * @return full path to provider's data directory
     */
    public String getProviderDirectory(Provider provider) {
        return dataDirectory + "/" + provider.getValue();
    }
}
