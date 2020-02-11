/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.user;

import com.geoffgranum.uttu.core.persistence.id.TypedId;
import com.geoffgranum.uttu.iam.domain.identity.group.MemberOfGroupId;

import java.math.BigInteger;


public final class UserId extends TypedId<User> implements MemberOfGroupId {

  public UserId(BigInteger value) {
    super(value);
  }
}
 
