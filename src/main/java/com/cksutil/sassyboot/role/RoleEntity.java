package com.cksutil.sassyboot.role;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = false)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "role")
public class RoleEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "subscription_id")
    private UUID subscriptionId;

    @Column(name = "is_sys_role")
    private boolean isSysRole;

    @Column(name = "is_read_only", insertable = false, updatable = false)
    private boolean isReadOnly;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "role_has_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
    private Set<PermissionEntity> permissions;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "role_can_grant_roles",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "can_grant_role", referencedColumnName = "id"))
    private Set<RoleEntity> canGrantRoles;
}
