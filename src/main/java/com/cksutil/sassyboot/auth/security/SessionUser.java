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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

@Getter
public class SessionUser implements UserDetails, CredentialsContainer {
    private final UUID userId;
    private final String name;
    private final String username;

    @JsonIgnore
    private String password;

    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public SessionUser(UUID userId, String name, String username, String password, Set<GrantedAuthority> authorities,
                boolean accountNonExpired, boolean accountNonLocked,boolean credentialsNonExpired,boolean enabled) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired=credentialsNonExpired;
        this.enabled=enabled;
    }

    @Override
    public void eraseCredentials() {
        password=null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("User Id: ").append(this.userId.toString()).append("; ");
        sb.append("Name: ").append(this.name).append("; ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");

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
            return userId.equals(rhsSu.userId);
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
        return result;
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new SessionUser.AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority,
                    "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>,Serializable {
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

    public static SessionUserBuilder builder() {
        return new SessionUserBuilder();
    }

    public static class SessionUserBuilder {
        private UUID userId;
        private String name;
        private String username;
        private Set<GrantedAuthority> authorities;
        private boolean isAccountExpired;
        private boolean isAccountLocked;
        private String password;

        SessionUserBuilder() {
            authorities=new HashSet<>();
        }

        public SessionUserBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public SessionUserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SessionUserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public SessionUserBuilder withAuthorities(Set<GrantedAuthority> authorities) {
            this.authorities.addAll(authorities);
            return this;
        }
        public SessionUserBuilder withAuthoritiy(String authoritiy) {
            this.authorities.add(new SimpleGrantedAuthority(authoritiy));
            return this;
        }

        public SessionUserBuilder isAccountExpired(boolean isAccountExpired) {
            this.isAccountExpired = isAccountExpired;
            return this;
        }

        public SessionUserBuilder isAccountLocked(boolean isAccountLocked) {
            this.isAccountLocked = isAccountLocked;
            return this;
        }

        public SessionUserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public SessionUserBuilder withRole(String role) {
                Assert.isTrue(!role.startsWith("ROLE_"), () -> role
                        + " cannot start with ROLE_ (it is automatically added)");
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            return this;
        }

        public SessionUser build() {
            return new SessionUser(userId, name, username,password, authorities,
                    !isAccountExpired, !isAccountLocked,true,true);
        }
    }
}
