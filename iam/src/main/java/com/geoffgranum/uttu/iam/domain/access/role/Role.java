/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access.role;


import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.persistence.id.Identified;
import com.geoffgranum.uttu.iam.domain.access.IamPermission;
import com.geoffgranum.uttu.iam.domain.access.RevocablePermission;
import com.geoffgranum.uttu.iam.domain.identity.group.Group;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;

import static com.geoffgranum.uttu.core.base.Verify.hasLength;
import static com.geoffgranum.uttu.core.base.Verify.isNotEmpty;

/**
 * @author Geoff M. Granum
 */
public class Role implements Identified, SecurityRole {

  public static final Set<IamPermission> DEFAULT_SYSTEM_ADMIN_PERMISSIONS = ImmutableSet.copyOf(IamPermission.values());
  public static final Set<IamPermission> DEFAULT_TENANT_ADMIN_PERMISSIONS = new ImmutableSet.Builder<IamPermission>()
      .add(IamPermission.VIEW_TENANT)
      .add(IamPermission.VIEW_USER)
      .add(IamPermission.PROVISION_ROLE)
      .add(IamPermission.PROVISION_USER)
      .add(IamPermission.ADD_USER_TO_GROUP)
      .add(IamPermission.OFFER_REGISTRATION_INVITATION)
      .build();
  public final RoleId id;
  public final TenantId tenantId;
  public final String name;
  public final String description;
  public final Group group;
  public final Boolean supportsNesting;
  public final Set<RevocablePermission> permissions;

  private Role(Builder builder) {
    id = builder.id;
    tenantId = builder.tenantId;
    name = builder.name;
    description = builder.description;
    group = builder.group;
    supportsNesting = builder.supportsNesting;
    permissions = ImmutableSet.copyOf(builder.permissions);
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Role other = (Role) obj;
    return Objects.equal(this.id, other.id) && Objects.equal(this.tenantId, other.tenantId);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, tenantId);
  }

  @Override
  public RoleId id() {
    return id;
  }


  public Role.Builder copy() {
    return new Builder().copyOf(this);
  }

  public static final class Builder {

    private RoleId id;
    private TenantId tenantId;
    private String name;
    private String description;
    private Boolean supportsNesting;
    private Set<RevocablePermission> permissions = Sets.newHashSet();
    private transient boolean isCreate;
    private transient Group group;

    /*
          Role role = new Role.Builder()
          .tenantId( input.getTenantId() )
          .name( input.getName() )
          .description( input.getDescription() )
          .group( input.getGroup() )
          .supportsNesting( input.getSupportsNesting() )
          .permissions( input.getPermissions() )
          .build();*/
    public Builder() {
    }

    private static Group createInternalGroup(TenantId tenantId, String name) {
      String groupName = Group.ROLE_GROUP_PREFIX + UUID.randomUUID().toString().toUpperCase();
      Group group = new Group.Builder()
          .tenantId(tenantId)
          .name(groupName)
          .description("Role backing group for: " + name)
          .internal(true)
          .create()
          .build();
      return group;
    }

    public Role build() {
      Verify.isNotNull(id, "An id is required, even when creating a new Role.");
      Verify.isNotNull(tenantId, "The tenant id is required.");
      isNotEmpty(name, "Role name must be provided.");
      hasLength(name, 1, 100, "Role name must be 100 characters or less.");
      isNotEmpty(description, "Role description is required.");
      hasLength(description, 1, 250, "Role description must be 250 characters or less.");

      if (isCreate) {
        group = createInternalGroup(this.tenantId, this.name);
        isCreate = false;
      }
      return new Role(this);
    }

    public Builder copyOf(Role copy) {
      id = copy.id;
      tenantId = copy.tenantId;
      name = copy.name;
      description = copy.description;
      group = copy.group;
      supportsNesting = copy.supportsNesting;
      permissions = copy.permissions;
      return this;
    }

    public Builder create() {
      isCreate = true;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    //        public Builder from(BasicDBObject mongo) {
    //            this.id(new RoleId(mongo.getObjectId("_id")));
    //            this.tenantId(new TenantId(mongo.getObjectId("tenantId")));
    //            this.name(mongo.getString("name"));
    //            this.description(mongo.getString("description"));
    //            this.supportsNesting(mongo.getBoolean("supportsNesting"));
    //
    //            Set<RevocablePermission> permissionItems = Sets.newHashSet();
    //            BasicDBList list = (BasicDBList) mongo.get("permissions");
    //            for (Object item : list) {
    //                permissionItems.add(RevocablePermission.from((BasicDBObject) item));
    //            }
    //            this.permissions(permissionItems);
    //
    //            return this;
    //        }

    public Builder group(Group group) {
      this.group = group;
      return this;
    }

    public Builder id(RoleId id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder permissions(Set<RevocablePermission> permissions) {
      this.permissions = permissions;
      return this;
    }

    public Builder supportsNesting(Boolean supportsNesting) {
      this.supportsNesting = supportsNesting;
      return this;
    }

    public Builder tenantId(TenantId tenantId) {
      this.tenantId = tenantId;
      return this;
    }
  }
}
 
