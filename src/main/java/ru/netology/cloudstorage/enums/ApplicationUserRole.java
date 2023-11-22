package ru.netology.cloudstorage.enums;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static ru.netology.cloudstorage.enums.ApplicationUserPermission.FILE_DELETE;
import static ru.netology.cloudstorage.enums.ApplicationUserPermission.FILE_DOWNLOAD;
import static ru.netology.cloudstorage.enums.ApplicationUserPermission.FILE_MODERATE;
import static ru.netology.cloudstorage.enums.ApplicationUserPermission.FILE_READ;
import static ru.netology.cloudstorage.enums.ApplicationUserPermission.FILE_UPLOAD;

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