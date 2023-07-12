package com.iroshnk.nftraffle.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

public class RolePrefixGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 610L;
    private final String role;

    public RolePrefixGrantedAuthority(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }

    public String getAuthority() {
        if (this.role.startsWith("ROLE_")) {
            return this.role;
        } else {
            return "ROLE_" + this.role;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof SimpleGrantedAuthority && this.role.equals(((RolePrefixGrantedAuthority) obj).role);
        }
    }

    public int hashCode() {
        return this.role.hashCode();
    }

    public String toString() {
        return this.role;
    }
}
