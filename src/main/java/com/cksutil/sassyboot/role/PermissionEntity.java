package com.cksutil.sassyboot.role;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = false)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "permission")
public class PermissionEntity {
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

    @Column(name = "parent_permission")
    @GeneratedValue
    private UUID parentPermissionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_permission", insertable = false, updatable = false)
    private PermissionEntity parentPermission;
}
