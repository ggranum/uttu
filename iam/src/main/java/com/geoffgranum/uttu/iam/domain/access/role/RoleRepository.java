/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access.role;

import com.geoffgranum.uttu.iam.domain.ApplicationRepository;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.geoffgranum.uttu.iam.domain.identity.user.User;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends ApplicationRepository {

  public void add(Role role);

  void update(Role role);

  public Set<Role> allRoles(TenantId tenantId);

  public Set<Role> rolesForUser(User user);

  public void remove(Role role);

  public Optional<Role> named(TenantId tenantId, String roleName);
}
