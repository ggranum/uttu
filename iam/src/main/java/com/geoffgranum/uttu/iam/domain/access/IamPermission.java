/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public enum IamPermission {
  PROVISION_TENANT(1001, Key.PROVISION_TENANT),
  ACTIVATE_TENANT(1002, Key.ACTIVATE_TENANT),
  DEACTIVATE_TENANT(1003, Key.DEACTIVATE_TENANT),
  ADD_USER_TO_GROUP(10001, Key.ADD_USER_TO_GROUP),
  PROVISION_ROLE(10002, Key.PROVISION_ROLE),
  PROVISION_USER(10003, Key.PROVISION_USER),
  OFFER_REGISTRATION_INVITATION(10004, Key.OFFER_REGISTRATION_INVITATION),
  VIEW_TENANT(10005, Key.VIEW_TENANT),
  VIEW_USER(10006, Key.VIEW_USER);

  private static final Map<String, IamPermission> allByKey;

  static {
    ImmutableMap.Builder<String, IamPermission> builder = new ImmutableMap.Builder<>();
    for (IamPermission iamPermission : values()) {
      builder.put(iamPermission.permission.name, iamPermission);
    }
    allByKey = builder.build();
  }

  public final PermissionId id;
  // Enums have an internal concept of name that risks unfortunate results if we mix terminology.
  public final String key;
  public final Permission permission;

  private IamPermission(int id, String key) {
    this.id = new PermissionId(BigInteger.valueOf(id));
    this.key = key;
    permission = new Permission(this.id, key);
  }

  public static Set<Permission> all() {
    return asPermissions(Arrays.asList(values()));
  }

  public static Set<RevocablePermission> asRevocable(Iterable<IamPermission> iamPermissions, boolean revoke) {
    Set<RevocablePermission> permissions = Sets.newHashSet();
    for (IamPermission iamPermission : iamPermissions) {
      permissions.add(new RevocablePermission(iamPermission.permission, revoke));
    }
    return permissions;
  }

  public static Set<RevocablePermission> allAsUserPermissions(boolean revoke) {
    return asRevocable(allByKey.values(), revoke);
  }

  public static IamPermission forPermissionKey(String key) {
    return allByKey.get(key);
  }

  private static Set<Permission> asPermissions(Iterable<IamPermission> iamPermissions) {
    Set<Permission> permissions = Sets.newHashSet();
    for (IamPermission iamPermission : iamPermissions) {
      permissions.add(iamPermission.permission);
    }
    return permissions;
  }

  public static class Key {

    public static final String PROVISION_TENANT = "Provision Tenant";
    public static final String ACTIVATE_TENANT = "Activate Tenant";
    public static final String DEACTIVATE_TENANT = "Deactivate Tenant";

    public static final String PROVISION_USER = "Provision User";
    public static final String PROVISION_ROLE = "Provision Role";
    public static final String ADD_USER_TO_GROUP = "Add User to Group";
    public static final String OFFER_REGISTRATION_INVITATION = "Offer registration invitation";
    public static final String VIEW_TENANT = "View Tenant";
    public static final String VIEW_USER = "View User";
  }
}
 
