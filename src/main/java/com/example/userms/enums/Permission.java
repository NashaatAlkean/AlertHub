package com.example.userms.enums;

public enum Permission {
    CREATE_ACTION("createAction"),
    UPDATE_ACTION("updateAction"),
    DELETE_ACTION("deleteAction"),
    CREATE_METRIC("createMetric"),
    UPDATE_METRIC("updateMetric"),
    DELETE_METRIC("deleteMetric"),
    TRIGGER_SCAN("triggerScan"),
    TRIGGER_PROCESS("triggerProcess"),
    TRIGGER_EVALUATION("triggerEvaluation"),
    READ("read");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public static Permission fromString(String permission) {
        for (Permission p : Permission.values()) {
            if (p.permission.equalsIgnoreCase(permission)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown permission: " + permission);
    }
}
