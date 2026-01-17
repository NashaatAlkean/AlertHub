package com.alerthub.enums;

import lombok.Getter;

@Getter
public enum Label {
    BUG("bug", "Something isn't working"),
    DOCUMENTATION("documentation", "Improvements or additions to documentation"),
    DUPLICATE("duplicate", "This issue or pull request already exists"),
    ENHANCEMENT("enhancement", "New feature or request"),
    GOOD_FIRST_ISSUE("good_first_issue", "Good for newcomers"),
    HELP_WANTED("help_wanted", "Extra attention is needed"),
    INVALID("invalid", "This doesn't seem right"),
    QUESTION("question", "Further information is requested"),
    WONTFIX("wontfix", "This will not be worked on");

    private final String value;
    private final String description;

    Label(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static Label fromValue(String value) {
        for (Label label : Label.values()) {
            if (label.value.equalsIgnoreCase(value)) {
                return label;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + value);
    }
}