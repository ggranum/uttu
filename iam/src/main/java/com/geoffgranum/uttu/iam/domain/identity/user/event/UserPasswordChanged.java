/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.user.event;


import com.geoffgranum.uttu.core.guava.DomainEvent;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;

/**
 * @author Geoff M. Granum
 */
public class UserPasswordChanged extends DomainEvent {

  private final TenantId tenantId;
  private final String username;

  public UserPasswordChanged(TenantId tenantId, String username) {
    super();
    this.tenantId = tenantId;
    this.username = username;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }

  public String username() {
    return username;
  }
}
 
