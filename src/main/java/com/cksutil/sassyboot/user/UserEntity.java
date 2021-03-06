package com.cksutil.sassyboot.user;

import com.cksutil.sassyboot.role.RoleEntity;
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
@Table(name = "sbt_user")
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "failed_login_attempted")
    private int failedLoginAttempt;

    @Column(name = "app_role")
    @GeneratedValue
    private UUID appRoleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_role", insertable = false, updatable = false)
    private RoleEntity appRole;

}
