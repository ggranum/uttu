/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.user;

import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.persistence.id.IdGenerator;
import com.geoffgranum.uttu.core.persistence.id.Identified;
import com.geoffgranum.uttu.iam.domain.access.RevocablePermission;
import com.geoffgranum.uttu.iam.domain.identity.Enablement;
import com.geoffgranum.uttu.iam.domain.identity.EncryptionToken;
import com.geoffgranum.uttu.iam.domain.identity.group.Group;
import com.geoffgranum.uttu.iam.domain.identity.group.GroupMember;
import com.geoffgranum.uttu.iam.domain.identity.group.GroupMemberType;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import static com.geoffgranum.uttu.core.base.Verify.hasLength;
import static com.geoffgranum.uttu.core.base.Verify.isNotEmpty;

/**
 * A User represents a person or other 'subject' that can be authenticated.
 *
 * @author Geoff M. Granum
 */

public class User implements Identified {

  public static final String UNIQUE_USERNAMES_PROP_KEY = "com.geoffgranum.uttu.iam.unique-usernames";
  public final UserId id;
  public final TenantId tenantId;
  public final String username;
  public final String passwordHash;
  public final String saltHex;
  public final Enablement enablement;
  public final Set<RevocablePermission> permissions;

  private User(Builder builder) {

    enablement = builder.enablement;
    passwordHash = builder.passwordHash;
    saltHex = builder.saltHex;
    username = builder.username;
    tenantId = builder.tenantId;
    id = builder.id;
    permissions = builder.revocablePermissions;
  }


  @Override
  public UserId id() {
    return id;
  }

  public boolean isEnabled() {
    return this.enablement.isEnablementEnabled();
  }

  public GroupMember toGroupMember(Group parentGroup) {
    GroupMember groupMember = new GroupMember.Builder()
        .type(GroupMemberType.User)
        .tenantId(tenantId)
        .memberOfGroupId(id())
        .parentGroupId(parentGroup.id)
        .create()
        .build();
    return groupMember;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final User other = (User) obj;
    return Objects.equal(this.id, other.id);
  }

  public User.Builder copy() {
    return new Builder().copyOf(this);
  }
  //
  //  public DBObject toMongo() {
  //    BasicDBObject mongo = new BasicDBObject();
  //    mongo.append("_id", id.value());
  //    mongo.append("tenantId", tenantId.value());
  //    mongo.append("username", username);
  //    mongo.append("passwordHash", passwordHash);
  //    mongo.append("saltHex", saltHex);
  //    mongo.append("enablement", enablement.toMongo());
  //    BasicDBList userPermissions = new BasicDBList();
  //    for (RevocablePermission item : this.permissions) {
  //      userPermissions.add(item.toMongo());
  //    }
  //    mongo.append("userPermissions", userPermissions);
  //    return mongo;
  //  }

  public static final class Builder {

    private UserId id;
    private TenantId tenantId;
    private String username;
    private String passwordHash;
    private String saltHex;
    private Enablement enablement;
    // internal
    private Set<RevocablePermission> revocablePermissions;
    private transient String passwordClearText;
    private transient boolean isCreate;

    /*
          User user = new User.Builder()
          .enablement( input.getEnablement() )
          .person( input.getPerson() )
          .salt( input.getSalt() )
          .password( input.getPassword() )
          .username( input.getUsername() )
          .tenantId( input.getTenantId() )
          .id( input.getId() )
          .build();*/
    public Builder() {
    }

    //    public Builder from(BasicDBObject mongo) {
    //      this.id(new UserId(mongo.getObjectId("_id")));
    //      this.tenantId(new TenantId(mongo.getObjectId("tenantId")));
    //      this.username(mongo.getString("username"));
    //      this.passwordHash(mongo.getString("passwordHash"));
    //      this.saltHex(mongo.getString("saltHex"));
    //      this.enablement(Enablement.from((BasicDBObject) mongo.get("enablement")));
    //      Set<RevocablePermission> revocablePermissionItems = Sets.newHashSet();
    //      BasicDBList list = (BasicDBList) mongo.get("userPermissions");
    //      for (Object item : list) {
    //        revocablePermissionItems.add(RevocablePermission.from((BasicDBObject) item));
    //      }
    //      this.userPermissions(revocablePermissionItems);
    //      return this;
    //    }

    public Builder userPermissions(Set<RevocablePermission> revocablePermissions) {
      this.revocablePermissions = revocablePermissions;
      return this;
    }

    public Builder copyOf(User copy) {
      id = copy.id;
      tenantId = copy.tenantId;
      username = copy.username;
      passwordHash = copy.passwordHash;
      saltHex = copy.saltHex;
      enablement = copy.enablement;
      revocablePermissions = copy.permissions;
      return this;
    }

    public Builder enablement(Enablement enablement) {
      this.enablement = enablement;
      return this;
    }

    public Builder saltHex(String salt) {
      this.saltHex = salt;
      return this;
    }

    public Builder passwordClearText(String passwordClearText) {
      this.passwordClearText = passwordClearText;
      return this;
    }

    public Builder passwordHash(String password) {
      this.passwordHash = password;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder tenantId(TenantId tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public Builder id(UserId id) {
      this.id = id;
      return this;
    }

    public Builder create() {
      this.isCreate = true;
      return this;
    }

    public User create(IdGenerator idGen) {
      this.id((new UserId(idGen.next())));
      return build();
    }

    public User build() {
      Verify.isNotNull(id, "Id is required, even for create.");
      Verify.isNotNull(tenantId, "Tenant Id is required.");
      isNotEmpty(username, "Username is required.");
      hasLength(username, 1, 100, "Username must be 100 characters or less.");
      Verify.isNotNull(enablement, "The enablement is required.");
      Verify.isNotNull(revocablePermissions, "Permissions are required, but may be empty.");
      if (isCreate) {
        EncryptionToken token = this.protectPassword(username, passwordClearText);
        this.passwordHash = token.hashAsHexString;
        this.saltHex = token.saltAsHexString;
        isCreate = false;
      } else if (StringUtils.isNotEmpty(passwordClearText)) {
        isNotEmpty(saltHex, "User's password salt is required.");
        EncryptionToken token = EncryptionToken.fromPasswordClearText(passwordClearText, saltHex);
        passwordHash = token.hashAsHexString;
      }
      return new User(this);
    }

    private EncryptionToken protectPassword(String username, String passwordClearText) {
      Verify.isNotEmpty(passwordClearText, "Password is required.");
      this.checkPasswordNotWeak(passwordClearText);
      this.checkUsernamePasswordNotSame(username, passwordClearText);
      return EncryptionToken.fromPasswordClearText(passwordClearText);
    }

    private void checkUsernamePasswordNotSame(String username, String passwordClearText) {
      Verify.isNotEqual(username,
          passwordClearText,
          "Username and password cannot be the same.");
    }

    private void checkPasswordNotWeak(String passwordClearText) {
      Verify.isFalse(BasicPasswordService.isWeak(passwordClearText), "The password is too weak.");
    }
  }
}
 
