package org.loader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for GitHub repository integration.
 * Maps to loader.github.repo.* properties in application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "loader.github.repo")
@Data
public class GitHubConfig {

    /**
     * GitHub repository owner/organization name
     */
    private String owner = "teamMST";

    /**
     * GitHub repository name
     */
    private String name = "MST_AlertHub";

    /**
     * GitHub branch to download files from
     */
    private String branch = "main";

    /**
     * GitHub Personal Access Token (optional, for private repos)
     */
    private String token;

    /**
     * Base URL for GitHub raw content
     */
    private static final String RAW_CONTENT_URL = "https://raw.githubusercontent.com";

    /**
     * Base URL for GitHub API
     */
    private static final String API_URL = "https://api.github.com";

    /**
     * Get the full repository path (owner/repo)
     */
    public String getRepoPath() {
        return owner + "/" + name;
    }

    /**
     * Get raw file URL for a specific file
     */
    public String getRawFileUrl(String filePath) {
        return String.format("%s/%s/%s/%s/%s",
                RAW_CONTENT_URL, owner, name, branch, filePath);
    }

    /**
     * Get API URL for listing directory contents
     */
    public String getContentsApiUrl(String path) {
        return String.format("%s/repos/%s/%s/contents/%s?ref=%s",
                API_URL, owner, name, path, branch);
    }
}