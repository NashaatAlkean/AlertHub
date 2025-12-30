package com.contracts.enums;

public enum RunOnDay {
    ALL,
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY

    public boolean matches(DayOfWeek dayOfWeek) {
        if (this == ALL) return true;
        return switch (dayOfWeek) {
            case SUNDAY -> this == SUNDAY;
            case MONDAY -> this == MONDAY;
            case TUESDAY -> this == TUESDAY;
            case WEDNESDAY -> this == WEDNESDAY;
            case THURSDAY -> this == THURSDAY;
            case FRIDAY -> this == FRIDAY;
            case SATURDAY -> this == SATURDAY;
        };
    }
}
