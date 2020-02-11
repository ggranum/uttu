/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.group;


import com.geoffgranum.uttu.iam.domain.identity.user.UserId;

import java.math.BigInteger;

/**
 * @author Geoff M. Granum
 */
public enum GroupMemberType {
  Group {
    @Override
    public boolean isGroup() {
      return true;
    }

    @Override
    public GroupId memberOfGroupId(BigInteger memberOfGroupId) {
      return new GroupId(memberOfGroupId);
    }
  },

  User {
    @Override
    public boolean isUser() {
      return true;
    }

    @Override
    public UserId memberOfGroupId(BigInteger memberOfGroupId) {
      return new UserId(memberOfGroupId);
    }
  };

  public boolean isGroup() {
    return false;
  }

  public boolean isUser() {
    return false;
  }

  public MemberOfGroupId memberOfGroupId(BigInteger memberOfGroupId) {
    throw new IllegalStateException("New Group Member Types must override memberOfGroupId");
  }
}
