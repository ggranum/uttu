/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.group;


import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.persistence.id.Identified;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.Set;

import static com.geoffgranum.uttu.core.base.Verify.hasLength;
import static com.geoffgranum.uttu.core.base.Verify.isNotEmpty;

/**
 * @author Geoff M. Granum
 */
public final class Group implements Identified {

  /* Roles utilize 'hidden' groups to support their responsibilities. */
  public static final String ROLE_GROUP_PREFIX = "ROLE-INTERNAL-GROUP: ";

  public final GroupId id;
  public final TenantId tenantId;
  public final String name;
  public final String description;
  public final Boolean internal;
  public final Set<GroupMember> groupMembers;

  private Group(Builder builder) {
    groupMembers = builder.groupMembers;
    internal = builder.internal;
    description = builder.description;
    name = builder.name;
    tenantId = builder.tenantId;
    id = builder.id;
  }


  public boolean isInternalGroup() {
    return internal;
  }

  public Set<GroupMember> groupMembers() {
    return Collections.unmodifiableSet(this.groupMembers);
  }

  @Override
  public GroupId id() {
    return id;
  }


  public String name() {
    return this.name;
  }

  GroupMember toGroupMemberOf(Group group) {
    GroupMember groupMember = new GroupMember.Builder()
        .type(GroupMemberType.Group)
        .tenantId(tenantId)
        .memberOfGroupId(id)
        .parentGroupId(group.id)
        .build();
    return groupMember;
  }

  //  public DBObject toMongo() {
  //    BasicDBObject mongo = new BasicDBObject();
  //    mongo.append("_id", id.value());
  //    mongo.append("tenantId", tenantId.value());
  //    mongo.append("description", description);
  //    mongo.append("name", name);
  //    mongo.append("internal", internal);
  //    BasicDBList groupMembers = new BasicDBList();
  //    for (GroupMember item : this.groupMembers) {
  //      groupMembers.add(item.toMongo());
  //    }
  //    mongo.append("groupMembers", groupMembers);
  //    return mongo;
  //  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Group group = (Group) o;

    if (!id.equals(group.id)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public Builder copy() {
    return new Builder().copyOf(this);
  }

  public static final class Builder {

    private GroupId id;
    private TenantId tenantId;
    private String description;
    private String name;
    private boolean internal;
    private Set<GroupMember> groupMembers = Sets.newHashSet();
    private transient boolean isCreate;

    /*
      Group group = new Group.Builder()
      .groupMembers( input.getGroupMembers() )
      .internal( input.getInternal() )
      .description( input.getDescription() )
      .name( input.getName() )
      .tenantId( input.getTenantId() )
      .id( input.getId() )
      .build();*/
    @Inject
    public Builder() {
    }

    public Builder copyOf(Group copy) {
      groupMembers = copy.groupMembers;
      internal = copy.internal;
      description = copy.description;
      name = copy.name;
      tenantId = copy.tenantId;
      id = copy.id;
      return this;
    }

    public Builder groupMembers(Set<GroupMember> groupMembers) {
      this.groupMembers = groupMembers;
      return this;
    }

    public Builder internal(Boolean internal) {
      this.internal = internal;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder tenantId(TenantId tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public Builder id(GroupId id) {
      this.id = id;
      return this;
    }

    public Builder create() {
      isCreate = true;
      return this;
    }

    public Group build() {
      Verify.isNotNull(id, "Id is required, even when creating.");
      Verify.isNotNull(tenantId, "Tenant Id is required.");
      isNotEmpty(name, "Name is required.");
      hasLength(name, 1, 100, "Group name must be 100 characters or less.");
      hasLength(description, 0, 250, "Group description must be 250 characters or less.");
      if (isCreate) {
        isCreate = false;
      }
      Verify.isNotNull(id, "Id must be set when create is not specified");
      return new Group(this);
    }

    //    public Builder from(BasicDBObject mongo) {
    //      this.id(new GroupId(mongo.getObjectId("_id")));
    //      this.tenantId(new TenantId(mongo.getObjectId("tenantId")));
    //      this.description(mongo.getString("description"));
    //      this.name(mongo.getString("name"));
    //      this.internal(mongo.getBoolean("internal"));
    //      Set<GroupMember> newGroupMemberItems = Sets.newHashSet();
    //      BasicDBList list = (BasicDBList)mongo.get("groupMembers");
    //      for (Object item : list) {
    //        newGroupMemberItems.add(new GroupMember.Builder().from((BasicDBObject)item).build());
    //      }
    //      this.groupMembers(newGroupMemberItems);
    //      return this;
    //    }

    public Builder addGroupMember(GroupMember newMember) {
      this.groupMembers.add(newMember);
      return this;
    }

    public Builder removeGroupMember(GroupMember childGroupMember) {
      this.groupMembers.remove(childGroupMember);
      return this;
    }

  }
}
 
