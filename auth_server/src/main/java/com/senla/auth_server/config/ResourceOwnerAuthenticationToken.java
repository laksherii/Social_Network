package com.senla.auth_server.config;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class ResourceOwnerAuthenticationToken extends AbstractAuthenticationToken {
    @Getter
    private final Long id;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public ResourceOwnerAuthenticationToken(Long id, String username, String password, List<GrantedAuthority> authorities) {
        super(authorities);
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        setAuthenticated(true);
    }

    public ResourceOwnerAuthenticationToken(String username, String password) {
        super(null);
        this.id = null;
        this.username = username;
        this.password = password;
        this.authorities = List.of();
        setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}

