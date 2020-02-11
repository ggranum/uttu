/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.group;

import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;

import java.util.Optional;

/**
 * @author Geoff M. Granum
 */
public class GroupMember {

  public final TenantId tenantId;
  public final GroupMemberType type;
  public final MemberOfGroupId memberOfGroupId;
  public final GroupId parentGroupId;

  private GroupMember(Builder builder) {
    tenantId = builder.tenantId;
    type = builder.type;
    memberOfGroupId = builder.memberOfGroupId;
    parentGroupId = builder.parentGroupId.get();
  }

  public boolean isGroupType() {
    return this.type().isGroup();
  }

  public boolean isUserType() {
    return this.type().isUser();
  }

  public TenantId tenantId() {
    return this.tenantId;
  }

  public GroupMemberType type() {
    return this.type;
  }

  public MemberOfGroupId memberOfGroupId() {
    return type().memberOfGroupId(memberOfGroupId.value());
  }

  /**
   * Override equals to ignore the database ID, which is not relevant to value objects.
   */
  @Override
  public boolean equals(Object thatObject) {
    boolean objectsEqual = false;

    if (thatObject != null && this.getClass() == thatObject.getClass()) {
      GroupMember that = (GroupMember) thatObject;
      objectsEqual =
          this.tenantId().equals(that.tenantId()) &&
              this.type().equals(that.type()) &&
              this.memberOfGroupId().equals(that.memberOfGroupId());
    }

    return objectsEqual;
  }

  @Override
  public int hashCode() {
    int hashCodeValue =
        +(21941 * 197)
            + this.tenantId().hashCode()
            + this.type().hashCode();

    return hashCodeValue;
  }

  //  public DBObject toMongo() {
  //    BasicDBObject mongo = new BasicDBObject();
  //    mongo.append("tenantId", tenantId.value());
  //    mongo.append("type", type.toString());
  //    mongo.append("memberOfGroupId", memberOfGroupId.value());
  //    mongo.append("parentGroupId", parentGroupId.value());
  //    return mongo;
  //  }

  public static final class Builder {

    private TenantId tenantId;
    private GroupMemberType type;
    private MemberOfGroupId memberOfGroupId;
    private Optional<GroupId> parentGroupId;
    private transient boolean isCreate;

    /*
      GroupMember groupMember = new GroupMember.Builder()
      .id( input.getId() )
      .tenantId( input.getTenantId() )
      .type( input.getType() )
      .build();*/
    public Builder() {
    }

    public Builder copyOf(GroupMember copy) {
      tenantId = copy.tenantId;
      type = copy.type;
      return this;
    }

    public Builder tenantId(TenantId tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public Builder type(GroupMemberType type) {
      this.type = type;
      return this;
    }

    public Builder memberOfGroupId(MemberOfGroupId memberOfGroupId) {
      this.memberOfGroupId = memberOfGroupId;
      return this;
    }

    public Builder parentGroupId(GroupId parentGroupId) {
      this.parentGroupId = Optional.of(parentGroupId);
      return this;
    }

    public Builder create() {
      this.isCreate = true;
      return this;
    }

    public GroupMember build() {
      Verify.isNotNull(tenantId, "Tenant id is required.");
      Verify.isNotNull(type, "Group member type is required.");
      Verify.isNotNull(memberOfGroupId, "Group member id is required.");
      Verify.isNotNull(parentGroupId, "Parent group is required.");
      return new GroupMember(this);
    }

    //    public Builder from(BasicDBObject mongo) {
    //      this.tenantId(new TenantId(mongo.getObjectId("tenantId")));
    //      this.type(GroupMemberType.valueOf(mongo.getString("type")));
    //      this.memberOfGroupId(type.memberOfGroupId(mongo.getObjectId("memberOfGroupId")));
    //      this.parentGroupId(new GroupId(mongo.getObjectId("parentGroupId")));
    //      return this;
    //    }
  }
}
 
