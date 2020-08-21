package com.cksutil.sassyboot.auth.resource;

import com.cksutil.sassyboot.auth.security.SessionUser;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class SessionUserResource {
    private final UUID userId;
    private final String name;

    private final Map<String,Boolean> authorities;
    private final boolean isAccountExpired;
    private final boolean isAccountLocked;
    private final boolean isCredentialsExpired;
    private final boolean isEnabled;

    public static SessionUserResource build(SessionUser sessionUser){
        return SessionUserResource.builder()
                .userId(sessionUser.getUserId())
                .name(sessionUser.getName())
                .authorities(sessionUser.getAuthorities()
                        .stream()
                        .collect(Collectors.toMap(GrantedAuthority::getAuthority,ga->Boolean.TRUE)))
                .isAccountExpired(!sessionUser.isAccountNonExpired())
                .isAccountLocked(!sessionUser.isAccountNonLocked())
                .isCredentialsExpired(!sessionUser.isCredentialsNonExpired())
                .isEnabled(sessionUser.isEnabled())
                .build();
    }
}
