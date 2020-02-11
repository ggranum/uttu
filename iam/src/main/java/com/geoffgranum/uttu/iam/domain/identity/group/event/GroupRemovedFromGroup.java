/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.group.event;

import com.geoffgranum.uttu.core.guava.DomainEvent;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;

/**
 * @author Geoff M. Granum
 */
public class GroupRemovedFromGroup extends DomainEvent {

  private static final long serialVersionUID = 1L;
  private final TenantId tenantId;
  private final String groupName;
  private final String nestedGroupName;

  public GroupRemovedFromGroup(TenantId tenantId, String groupName, String nestedGroupName) {
    super(System.currentTimeMillis(), (int) serialVersionUID);

    this.tenantId = tenantId;
    this.groupName = groupName;
    this.nestedGroupName = nestedGroupName;
  }

  public String groupName() {
    return this.groupName;
  }

  public String nestedGroupName() {
    return this.nestedGroupName;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
