/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access;

import com.google.common.base.MoreObjects;

/**
 * The system has permissions such as CAN_DELETE_GROUP, users and roles have those permissions combined with an
 * enabled or disabled state.
 * <p/>
 * For example, if a Role has the permission "Can Delete Foo", and I want to add Sam but know that
 * he's going to screw everything up thanks to the delete rights, I can assign Sam to the Role and disable
 * the Delete permissions just for him.
 * <p/>
 * It also allows 'child' Roles to remove a permission from an inherited role, helping to avoid Role explosion.
 *
 * @author Geoff M. Granum
 */
public class RevocablePermission {

  public final Permission permission;
  public final boolean isRevocation;

  public RevocablePermission(Permission permission, boolean isRevocation) {
    this.permission = permission;
    this.isRevocation = isRevocation;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("isRevocation", isRevocation)
        .add("name", permission.name)
        .toString();
  }
}
 
