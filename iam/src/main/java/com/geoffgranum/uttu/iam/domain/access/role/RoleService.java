package com.geoffgranum.uttu.iam.domain.access.role;

import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.iam.domain.access.role.event.GroupAssignedToRole;
import com.geoffgranum.uttu.iam.domain.access.role.event.GroupUnassignedFromRole;
import com.geoffgranum.uttu.iam.domain.access.role.event.UserAssignedToRole;
import com.geoffgranum.uttu.iam.domain.access.role.event.UserUnassignedFromRole;
import com.geoffgranum.uttu.iam.domain.identity.group.Group;
import com.geoffgranum.uttu.iam.domain.identity.group.GroupService;
import com.geoffgranum.uttu.iam.domain.identity.user.User;
import com.google.common.eventbus.EventBus;

import javax.inject.Inject;

/**
 * @author ggranum
 */
public class RoleService {

  private final GroupService groupService;
  private final EventBus domainBus;

  @Inject
  private RoleService(GroupService groupService, EventBus domainBus) {
    this.groupService = groupService;
    this.domainBus = domainBus;
  }

  public Role assignUser(Role role, User user) {
    Verify.isNotNull(role, "Role must not be null.");
    Verify.isNotNull(user, "User must not be null.");
    Verify.equal(role.tenantId, user.tenantId, "Wrong tenant for this user.");

    Group nextGroup = groupService.addUser(role.group, user);
    Role nextRole = role.copy().group(nextGroup).build();


    domainBus.post(new UserAssignedToRole(nextRole.tenantId, nextRole.name, user.username));
    return nextRole;
  }

  public Role assignGroup(Role role, Group group) {
    Verify.isTrue(role.supportsNesting, IllegalStateException.class, "This role does not support group nesting.");
    Verify.isNotNull(group, "Group must not be null.");
    Verify.equal(role.tenantId, group.tenantId, "Wrong tenant for this group.");

    Group nextGroup = groupService.addGroup(role.group, group);
    Role nextRole = role.copy().group(nextGroup).build();
    domainBus.post(new GroupAssignedToRole(role.tenantId, role.name, group.name()));
    return nextRole;
  }


  public Role unassignGroup(Role target, Group group) {
    Verify.isTrue(target.supportsNesting, IllegalStateException.class, "This role does not support group nesting.");
    Verify.isNotNull(group, "Group must not be null.");
    Verify.equal(target.tenantId, group.tenantId, "Wrong tenant for this group.");

    Group nextGroup = groupService.removeGroup(target.group, group);
    Role nextRole = target.copy().group(nextGroup).build();

    domainBus.post(new GroupUnassignedFromRole(target.tenantId, target.name, group.name()));
    return nextRole;
  }

  public Role unassignUser(Role target, User user) {
    Verify.isNotNull(user, "User must not be null.");
    Verify.equal(target.tenantId, user.tenantId, "Wrong tenant for this user.");

    Group nextGroup = groupService.removeUser(target.group, user);
    Role nextRole = target.copy().group(nextGroup).build();

    domainBus.post(new UserUnassignedFromRole(nextRole.tenantId, nextRole.name, user.username));
    return nextRole;
  }

  public boolean isInRole(Role target, User user) {
    return isInRole(target, user, true);
  }

  public boolean isInRole(Role target, User user, boolean deep) {
    return groupService.hasMember(target.group, user, deep);
  }
}
