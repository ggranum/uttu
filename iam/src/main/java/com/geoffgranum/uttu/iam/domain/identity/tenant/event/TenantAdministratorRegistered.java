/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.tenant.event;

import com.geoffgranum.uttu.core.guava.DomainEvent;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.geoffgranum.uttu.iam.domain.identity.user.FullName;

public class TenantAdministratorRegistered extends DomainEvent {

  private final TenantId tenantId;
  private final String tenantName;
  private final FullName administratorName;
  private final String emailAddress;
  private final String username;

  public TenantAdministratorRegistered(
      TenantId tenantId,
      String tenantName,
      FullName administratorName,
      String emailAddress,
      String username) {
    super();
    this.tenantId = tenantId;
    this.tenantName = tenantName;
    this.administratorName = administratorName;
    this.emailAddress = emailAddress;
    this.username = username;
  }

  public TenantId tenantId() {
    return tenantId;
  }

  public String tenantName() {
    return tenantName;
  }

  public FullName administratorName() {
    return administratorName;
  }

  public String emailAddress() {
    return emailAddress;
  }

  public String username() {
    return username;
  }

  @Override
  public String toString() {
    String str = "TenantAdministratorRegistered{" +
        "tenantName='" + tenantName + '\'' +
        ", username='" + username + '\'';
    str += '}';
    return str;
  }
}
 
