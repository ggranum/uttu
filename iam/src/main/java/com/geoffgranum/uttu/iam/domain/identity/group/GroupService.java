package com.geoffgranum.uttu.iam.domain.identity.group;

import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.iam.domain.identity.group.event.GroupAddedToGroup;
import com.geoffgranum.uttu.iam.domain.identity.group.event.GroupRemovedFromGroup;
import com.geoffgranum.uttu.iam.domain.identity.group.event.GroupUserAdded;
import com.geoffgranum.uttu.iam.domain.identity.group.event.GroupUserRemoved;
import com.geoffgranum.uttu.iam.domain.identity.user.User;
import com.google.common.eventbus.EventBus;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;

import static com.geoffgranum.uttu.core.base.Verify.isTrue;

/**
 * @author ggranum
 */
public class GroupService {

  private final EventBus domainBus;
  private final GroupRepository groupRepository;

  @Inject
  private GroupService(EventBus domainBus, GroupRepository groupRepository) {
    this.domainBus = domainBus;
    this.groupRepository = groupRepository;
  }


  public Group addGroup(Group targetGroup, Group childGroup) {
    Verify.isNotNull(childGroup, "Group is required.");
    Verify.equal(targetGroup.tenantId, childGroup.tenantId, "Wrong tenant for this group.");
    Verify.isFalse(isMemberGroup(targetGroup, childGroup, targetGroup.toGroupMemberOf(targetGroup)),
        "Group recursion is not allowed.");
    Group result = targetGroup;
    GroupMember childGroupMember = childGroup.toGroupMemberOf(targetGroup);
    if (!targetGroup.groupMembers.contains(childGroupMember)) {
      result = new Group.Builder().copyOf(targetGroup).addGroupMember(childGroupMember).build();
      if (!targetGroup.internal) {
        domainBus.post(new GroupAddedToGroup(result.tenantId, result.name, childGroup.name));
      }
    }

    return result;
  }


  /**
   * Conly removes direct child groups, does not recurse.
   */
  public Group removeGroup(Group target, Group childGroup) {
    Verify.isNotNull(childGroup, "Group must not be null.");
    Verify.equal(target.tenantId, childGroup.tenantId, "Wrong tenant for this group.");
    Group result = target;
    GroupMember childGroupMember = childGroup.toGroupMemberOf(target);
    if (target.groupMembers.contains(childGroupMember)) {
      result = target.copy().removeGroupMember(childGroupMember).build();
      if (!target.internal) {
        domainBus.post(new GroupRemovedFromGroup(result.tenantId, result.name, childGroup.name()));
      }
    }
    return result;
  }

  public Group removeUser(Group target, User user) {
    Verify.isNotNull(user, "User is required.");
    Verify.equal(target.tenantId, user.tenantId, "Wrong tenant for this group.");
    Group result = target;
    GroupMember userMember = user.toGroupMember(target);
    // not a nested remove, only direct member
    if (target.groupMembers.contains(userMember)) {
      result = target.copy().removeGroupMember(userMember).build();
      if (!result.internal) {
        domainBus.post(new GroupUserRemoved(result.tenantId, result.name, user.username));
      }
    }
    return result;
  }

  public Group addUser(Group target, User user) {
    Verify.isNotNull(user, "User is required.");
    Verify.equal(target.tenantId, user.tenantId, "Wrong tenant for this group.");
    GroupMember userMember = user.toGroupMember(target);
    Group result = target;
    if (target.groupMembers.contains(userMember)) {
      result = target.copy().addGroupMember(userMember).build();
      if (!target.internal) {
        domainBus.post(new GroupUserAdded(target.tenantId, target.name, user.username));
      }
    }
    return result;
  }

  public boolean hasMember(Group target, User user) {
    return hasMember(target, user, true);
  }

  public boolean hasMember(Group target, User user, boolean deep) {
    Verify.isNotNull(user, "User is required");
    Verify.equal(target.tenantId, user.tenantId, "Wrong tenant for this group.");
    isTrue(user.isEnabled(), "User must be enabled.");

    GroupMember groupMember = user.toGroupMember(target);
    boolean isMember = target.groupMembers().contains(groupMember);

    if (!isMember && deep) {
      isMember = isUserInNestedGroup(target, user);
    }
    return isMember;
  }

  public boolean isUserInNestedGroup(Group group, User user) {
    boolean isInNestedGroup = false;

    Iterator<GroupMember> members = group.groupMembers().iterator();

    while (!isInNestedGroup && members.hasNext()) {
      GroupMember member = members.next();
      if (member.isGroupType()) {
        Optional<Group> nestedGroup = groupRepository.get((GroupId) member.memberOfGroupId());
        if (nestedGroup.isPresent()) {
          isInNestedGroup = hasMember(nestedGroup.get(), user);
        }
      }
    }
    return isInNestedGroup;
  }

  public boolean isMemberGroup(Group target, Group parentGroup, GroupMember group) {
    boolean isMember = false;

    Iterator<GroupMember> members = parentGroup.groupMembers().iterator();

    while (!isMember && members.hasNext()) {
      GroupMember member = members.next();
      if (member.isGroupType()) {
        if (group.equals(member)) {
          isMember = true;
        } else {
          Optional<Group> nestedGroup = groupRepository.get((GroupId) member.memberOfGroupId());
          if (nestedGroup.isPresent()) {
            isMember = isMemberGroup(target, nestedGroup.get(), group);
          }
        }
      }
    }

    return isMember;
  }


}
