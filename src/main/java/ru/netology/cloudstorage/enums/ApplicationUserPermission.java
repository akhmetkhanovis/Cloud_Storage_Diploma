package ru.netology.cloudstorage.enums;

public enum ApplicationUserPermission {
    FILE_READ("file:read"),
    FILE_UPLOAD("file:upload"),
    FILE_DOWNLOAD("file:download"),
    FILE_DELETE("file:delete"),
    FILE_MODERATE("file:moderate");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
