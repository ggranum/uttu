/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.group;


import com.geoffgranum.uttu.iam.domain.ApplicationRepository;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.geoffgranum.uttu.iam.domain.identity.user.UserId;

import java.util.Optional;
import java.util.Set;

/**
 * @author Geoff M. Granum
 */
public interface GroupRepository extends ApplicationRepository {

  void add(Group group);

  void update(Group group);

  Set<Group> allGroups(TenantId tenantId);

  Set<Group> allGroups(UserId id);

  Optional<Group> named(TenantId tenantId, String name);

  Optional<Group> get(GroupId groupId);

  void remove(Group group);
}
