package ru.netology.cloudstorage.enums;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static ru.netology.cloudstorage.enums.ApplicationUserPermission.*;
import static ru.netology.cloudstorage.enums.ApplicationUserPermission.FILE_DELETE;

public enum ApplicationUserRole {
    USER(Sets.newHashSet(FILE_READ, FILE_UPLOAD, FILE_DOWNLOAD, FILE_DELETE)),
    ADMIN(Sets.newHashSet(FILE_READ, FILE_UPLOAD, FILE_DOWNLOAD, FILE_DELETE, FILE_MODERATE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}