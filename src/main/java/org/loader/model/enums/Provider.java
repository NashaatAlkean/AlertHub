package org.loader.model.enums;
/**
 * Enum representing the different data providers for the Alert Hub system.
 */
public enum Provider {
    GITHUB("github"),
    JIRA("jira"),
    CLICKUP("clickup");

    private final String value;

    Provider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Convert string value to Provider enum.
     * Case-insensitive matching.
     *
     * @param value the string value to convert
     * @return the corresponding Provider enum
     * @throws IllegalArgumentException if the value doesn't match any provider
     */
    public static Provider fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider value cannot be null or empty");
        }

        for (Provider provider : Provider.values()) {
            if (provider.value.equalsIgnoreCase(value.trim())) {
                return provider;
            }
        }

        throw new IllegalArgumentException("Unknown provider: " + value);
    }
}
