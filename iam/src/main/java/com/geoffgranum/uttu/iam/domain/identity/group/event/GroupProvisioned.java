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
public class GroupProvisioned extends DomainEvent {

  private static final long serialVersionUID = 1L;
  private final TenantId tenantId;
  private final String groupName;

  public GroupProvisioned(TenantId tenantId, String groupName) {
    super(System.currentTimeMillis(), (int) serialVersionUID);

    this.tenantId = tenantId;
    this.groupName = groupName;
  }

  public String groupName() {
    return this.groupName;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
