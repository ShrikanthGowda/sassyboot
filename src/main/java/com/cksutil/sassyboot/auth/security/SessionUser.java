package com.cksutil.sassyboot.auth.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

@Getter
public class SessionUser implements UserDetails, CredentialsContainer {
    @JsonIgnore
    private final UUID userId;
    private final boolean firstLogin;

    @JsonIgnore
    private  String password;

    @JsonIgnore
    private final String username;

    private final String name;
    private final Set<GrantedAuthority> authorities;

    @JsonIgnore
    private final boolean accountNonExpired;

    @JsonIgnore
    private final boolean accountNonLocked;

    @JsonIgnore
    private final boolean credentialsNonExpired;

    @JsonIgnore
    private final boolean enabled;

    public SessionUser(UUID userId, boolean firstLogin, String password, String username,String name,
                       Collection<? extends GrantedAuthority> authorities, boolean accountNonExpired,
                       boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.userId = userId;
        this.firstLogin = firstLogin;
        this.password = password;
        this.username = username;
        this.name = name;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                new SessionUser.AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority,
                    "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }
    public void eraseCredentials() {
        password = null;
    }


    public static SessionUserBuilder builder() {
        return new SessionUserBuilder();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("User Id: ").append(this.userId.toString()).append("; ");
        sb.append("First Login: ").append(this.firstLogin).append("; ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
                .append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            boolean first = true;
            for (GrantedAuthority auth : authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(auth);
            }
        }
        else {
            sb.append("Not granted any authorities");
        }
        return sb.toString();
    }
    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof SessionUser) {
            SessionUser rhsSu = (SessionUser) rhs;
            return firstLogin == rhsSu.firstLogin
                    && userId.equals(rhsSu.userId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int PRIME = 173;
        int result = 1;
        final String $userName = this.getUsername();
        result = result * PRIME + ($userName == null ? 43 : $userName.hashCode());
        final UUID $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final boolean $firstLogin = this.isFirstLogin();
        result = result * PRIME + Boolean.hashCode($firstLogin);
        return result;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>,
            Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }


    public static class SessionUserBuilder {
        private UUID userId;
        private boolean firstLogin;
        private String password;
        private String username;
        private String name;
        private List<GrantedAuthority> authorities;
        private boolean accountExpired;
        private boolean accountLocked;
        private boolean credentialsExpired;
        private boolean disabled;

        private SessionUserBuilder() {
        }

        public SessionUserBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public SessionUserBuilder firstLogin(boolean firstLogin) {
            this.firstLogin = firstLogin;
            return this;
        }

        public SessionUserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public SessionUserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public SessionUserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SessionUserBuilder roles(String... roles) {
            List<GrantedAuthority> authorities = new ArrayList<>(
                    roles.length);
            for (String role : roles) {
                Assert.isTrue(!role.startsWith("ROLE_"), () -> role
                        + " cannot start with ROLE_ (it is automatically added)");
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return authorities(authorities);
        }
        public SessionUserBuilder authorities(List<GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public SessionUserBuilder accountExpired(boolean accountExpired) {
            this.accountExpired = accountExpired;
            return this;
        }

        public SessionUserBuilder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public SessionUserBuilder credentialsExpired(boolean credentialsExpired) {
            this.credentialsExpired = credentialsExpired;
            return this;
        }

        public SessionUserBuilder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public SessionUser build() {
            return new SessionUser(userId,firstLogin, password,
                    username,name, authorities, !accountExpired, !accountLocked, !credentialsExpired, !disabled);
        }
    }
}
