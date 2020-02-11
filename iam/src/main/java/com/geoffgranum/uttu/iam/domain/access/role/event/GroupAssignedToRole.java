/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access.role.event;


import com.geoffgranum.uttu.core.guava.DomainEvent;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;

/**
 * @author Geoff M. Granum
 */
public class GroupAssignedToRole extends DomainEvent {

  private final TenantId tenantId;
  private final String roleName;
  private final String groupName;

  public GroupAssignedToRole(TenantId tenantId, String roleName, String groupName) {
    super();
    this.tenantId = tenantId;
    this.roleName = roleName;
    this.groupName = groupName;
  }

  public String groupName() {
    return this.groupName;
  }

  public String roleName() {
    return this.roleName;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
