/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access.registration;


import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;

public class InvitationDescriptor {

  public final TenantId tenantId;
  public final String invitationId;
  public final String description;
  public final Long starting;
  public final Long until;

  public InvitationDescriptor(TenantId tenantId, String invitationId, String description, Long starting, Long until) {

    this.tenantId = tenantId;
    this.invitationId = invitationId;
    this.description = description;
    this.starting = starting;
    this.until = until;
  }
}
 
