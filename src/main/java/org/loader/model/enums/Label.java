package org.loader.model.enums;

/**
 * Enum representing different types of labels that can be assigned to tasks.
 * These labels categorize the nature of work items from various providers
 * (GitHub, Jira, ClickUp).
 */
public enum Label {
    /**
     * Something isn't working
     */
    BUG("bug"),

    /**
     * Improvements or additions to documentation
     */
    DOCUMENTATION("documentation"),

    /**
     * This issue or pull request already exists
     */
    DUPLICATE("duplicate"),

    /**
     * New feature or request
     */
    ENHANCEMENT("enhancement"),

    /**
     * Good for newcomers
     */
    GOOD_FIRST_ISSUE("good_first_issue"),

    /**
     * Extra attention is needed
     */
    HELP_WANTED("help_wanted"),

    /**
     * This doesn't seem right
     */
    INVALID("invalid"),

    /**
     * Further information is requested
     */
    QUESTION("question"),

    /**
     * This will not be worked on
     */
    WONTFIX("wontfix");

    private final String value;

    Label(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Convert string value to Label enum.
     * Case-insensitive matching.
     *
     * @param value the string value to convert
     * @return the corresponding Label enum, or null if not found
     */
    public static Label fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalizedValue = value.trim().toLowerCase().replace(" ", "_");

        for (Label label : Label.values()) {
            if (label.value.equalsIgnoreCase(normalizedValue)) {
                return label;
            }
        }

        return null;
    }
}




