/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access;

import com.geoffgranum.uttu.iam.domain.access.exception.PermissionRequiredException;
import com.geoffgranum.uttu.iam.domain.access.role.Role;
import com.geoffgranum.uttu.iam.domain.identity.tenant.Tenant;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.geoffgranum.uttu.iam.domain.identity.user.User;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import javax.annotation.concurrent.Immutable;
import java.util.Map;
import java.util.Set;

import static com.geoffgranum.uttu.core.base.Verify.isNotNull;
import static com.google.common.base.Preconditions.checkArgument;

@Immutable
public final class Subject {

  private static final ThreadLocal<Subject> SUBJECT_THREAD_LOCAL = new ThreadLocal<>();

  public final Tenant tenant;
  public final User user;
  public final Set<Role> roles;
  public final Map<String, RevocablePermission> permissions;

  private Subject(Builder builder) {
    tenant = builder.tenant;
    user = builder.user;
    roles = builder.roles;
    ImmutableMap.Builder<String, RevocablePermission> permBuilder = ImmutableMap.builder();
    for (RevocablePermission permission : builder.permissions) {
      permBuilder.put(permission.permission.name, permission);
    }
    permissions = permBuilder.build();
  }

  public static Subject currentSubject() {
    return SUBJECT_THREAD_LOCAL.get();
  }

  public void clear() {
    checkArgument(SUBJECT_THREAD_LOCAL.get() != null, "Subject was never applied to current thread.");
    SUBJECT_THREAD_LOCAL.remove();
  }

  public boolean isSystemUser() {
    return tenant.systemTenant;
  }

  public boolean hasTenant(TenantId tenantId) {
    return tenant.id().equals(tenantId);
  }

  public boolean isAnonymous() {
    return false;
  }

  public void checkPermitted(Permission permission) throws PermissionRequiredException {
    if (!isPermitted(permission)) {
      throw new PermissionRequiredException(permission, user.username);
    }
  }

  public boolean isPermitted(Permission permission) {
    boolean allowed = false;
    RevocablePermission revocablePermission = permissions.get(permission.name);
    if (revocablePermission != null && !revocablePermission.isRevocation) {
      allowed = true;
    }
    return allowed;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("tenant", tenant)
        .add("user", user)
        .toString();
  }

  public static final class Builder {

    private Tenant tenant;
    private User user;
    private Set<Role> roles;
    private Set<RevocablePermission> permissions;

    /*
      Subject subject = new Subject.Builder()
      .userId( input.getUserId() )
      .roles( input.getRoles() )
      .permissions( input.getPermissions() )
      .build();*/
    public Builder() {
    }

    public Builder tenant(Tenant tenant) {
      this.tenant = tenant;
      return this;
    }

    public Builder user(User user) {
      this.user = user;
      return this;
    }

    public Builder roles(Set<Role> roles) {
      this.roles = roles;
      return this;
    }

    public Builder permissions(Set<RevocablePermission> permissions) {
      this.permissions = permissions;
      return this;
    }

    public Subject build() {
      checkArgument(SUBJECT_THREAD_LOCAL.get() == null, "A Subject has already been applied to current thread.");
      isNotNull(roles, IllegalStateException.class, "Roles must be specified, but may be empty.");
      isNotNull(permissions, IllegalStateException.class, "Permissions must be specified, but may be empty.");
      isNotNull(user, IllegalStateException.class, "User must be specified.");
      isNotNull(tenant, IllegalStateException.class, "Tenant must be specified.");
      Subject subject = new Subject(this);
      SUBJECT_THREAD_LOCAL.set(subject);
      return subject;
    }
  }
}
 
