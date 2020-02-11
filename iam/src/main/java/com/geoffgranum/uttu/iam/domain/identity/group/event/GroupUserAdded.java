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
public class GroupUserAdded extends DomainEvent {

  private static final long serialVersionUID = 1L;
  private final TenantId tenantId;
  private final String groupName;
  private final String username;

  public GroupUserAdded(TenantId tenantId, String groupName, String username) {
    super();

    this.tenantId = tenantId;
    this.groupName = groupName;
    this.username = username;
  }

  public String groupName() {
    return this.groupName;
  }

  public String username() {
    return this.username;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
