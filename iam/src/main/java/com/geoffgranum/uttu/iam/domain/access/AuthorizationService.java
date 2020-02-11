/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access;

import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.iam.domain.access.role.Role;
import com.geoffgranum.uttu.iam.domain.access.role.RoleRepository;
import com.geoffgranum.uttu.iam.domain.access.role.RoleService;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.geoffgranum.uttu.iam.domain.identity.user.User;
import com.geoffgranum.uttu.iam.domain.identity.user.UserRepository;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.geoffgranum.uttu.core.base.Verify.isNotEmpty;

/**
 * @author Geoff M. Granum
 */
public class AuthorizationService {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final RoleService roleService;

  @Inject
  protected AuthorizationService(
      UserRepository userRepository,
      RoleRepository roleRepository,
      RoleService roleService) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.roleService = roleService;
  }

  public Set<Role> rolesForUser(User user) {
    return roleRepository.rolesForUser(user);
  }

  /**
   * Does not implement true role-group hierarchy yet, so do watch out. E.g. if a child group disables a
   * permission, it won't get applied to the user assigned to the child role as expected.
   *
   * @to`do ggranum: Fix nested role-group hierarchy.
   */
  public Set<RevocablePermission> permissionsForUser(User user) {
    Set<Role> roles = roleRepository.rolesForUser(user);
    Map<String, RevocablePermission> effectivePermissions;

    effectivePermissions = calculateEffectivePermissionsFromRoles(roles);

    // Add all the user's explicitly declared permissions. These always take precedence over any inherited from a
    // role, whether revoked or not.

    for (RevocablePermission revocablePermission : user.permissions) {
      effectivePermissions.put(revocablePermission.permission.name, revocablePermission);
    }

    return ImmutableSet.copyOf(effectivePermissions.values());
  }

  private Map<String, RevocablePermission> calculateEffectivePermissionsFromRoles(Set<Role> roles) {
    Map<String, RevocablePermission> effectivePermissions;
    effectivePermissions = Maps.newHashMap();

    /* As a partial, safe implementation of inheritable groups with revocable permissions,
     * user a list so we can accumulate an effective permissions list that ensures that a disabled permission
     * is ALWAYS disabled, no matter where in the hierarchy it's found. */
    List<RevocablePermission> rolePermissions = Lists.newArrayList();

    for (Role role : roles) {
      rolePermissions.addAll(role.permissions);
    }

    /**
     * Loop through the flat list, returning a Set that contains the unique permissions with respect to permission
     * name.
     * If a permission name is encountered twice (or more), but is disabled in one case and enabled in the other,
     * the User will ALWAYS receive the permission as disabled.
     */
    for (RevocablePermission rolePermission : rolePermissions) {
      RevocablePermission ancestorPermission = effectivePermissions.put(rolePermission.permission.name, rolePermission);
      if (ancestorPermission != null && ancestorPermission.isRevocation && !rolePermission.isRevocation) {
        // If it was in the map and was revoking the right, but the NEW item we just added did NOT revoke the right...
        effectivePermissions.put(ancestorPermission.permission.name, ancestorPermission); // then use the revoked perm.
      }
    }
    return effectivePermissions;
  }

  public boolean isUserInRole(TenantId tenantId, String username, String roleName) {
    Verify.isNotNull(tenantId, "TenantId must not be null.");
    isNotEmpty(username, "Username must not be provided.");
    isNotEmpty(roleName, "Role name must not be null.");
    Optional<User> user = userRepository.get(tenantId, username);
    return user.isPresent() && this.isUserInRole(user.get(), roleName);
  }

  public boolean isUserInRole(User user, String roleName) {
    Verify.isNotNull(user, "User must not be null.");
    isNotEmpty(roleName, "Role name must not be null.");

    boolean authorized = false;

    if (user.isEnabled()) {
      Optional<Role> role = roleRepository.named(user.tenantId, roleName);
      if (role.isPresent()) {
        authorized = roleService.isInRole(role.get(), user);
      }
    }
    return authorized;
  }
}
 
