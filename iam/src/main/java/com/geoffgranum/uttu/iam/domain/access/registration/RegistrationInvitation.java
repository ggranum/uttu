/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.access.registration;

import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.persistence.id.Identified;
import com.geoffgranum.uttu.iam.domain.identity.tenant.TenantId;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Date;

import static com.geoffgranum.uttu.core.base.Verify.hasLength;
import static com.geoffgranum.uttu.core.base.Verify.isNotEmpty;

/**
 * @author Geoff M. Granum
 */
public class RegistrationInvitation implements Identified, Serializable {

  private static final long serialVersionUID = 1L;
  public final RegistrationInvitationId id;

  public final TenantId tenantId;
  public final String description;
  public final String invitationToken;
  public final long startingOn;
  public final long until;

  private RegistrationInvitation(Builder builder) {
    id = builder.id;
    tenantId = builder.tenantId;
    description = builder.description;
    invitationToken = builder.invitationToken;
    startingOn = builder.startingOn;
    until = builder.until;
  }

  @Override
  public RegistrationInvitationId id() {
    return id;
  }

  public boolean isAvailable() {
    boolean isAvailable = false;

    long time = (new Date()).getTime();
    if (time >= this.startingOn() && time <= this.until()) {
      isAvailable = true;
    }

    return isAvailable;
  }

  public boolean isIdentifiedBy(String invitationIdentifier) {
    boolean isIdentified = this.invitationToken.equals(invitationIdentifier);
    if (!isIdentified && this.description != null) {
      isIdentified = this.description.equals(invitationIdentifier);
    }
    return isIdentified;
  }

  public RegistrationInvitation.Builder openEnded() {
    return new Builder()
        .tenantId(tenantId)
        .invitationToken(this.invitationToken)
        .description(description)
        .startingOn(System.currentTimeMillis())
        .until(Long.MAX_VALUE)
        .create();
  }

  public Long startingOn() {
    return this.startingOn;
  }

  public InvitationDescriptor toDescriptor() {
    return new InvitationDescriptor(
        this.tenantId,
        this.invitationToken,
        this.description,
        this.startingOn(),
        this.until());
  }

  public long until() {
    return this.until;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, tenantId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final RegistrationInvitation other = (RegistrationInvitation) obj;
    return Objects.equal(this.id, other.id) && Objects.equal(this.tenantId, other.tenantId);
  }

  public static final class Builder {

    private RegistrationInvitationId id;
    private TenantId tenantId;
    private String description = "";
    private String invitationToken;
    private Long startingOn;
    private Long until;
    private transient boolean isCreate;


    public Builder() {
    }


    public Builder(RegistrationInvitation copy) {
      id = copy.id;
      tenantId = copy.tenantId;
      description = copy.description;
      invitationToken = copy.invitationToken;
      startingOn = copy.startingOn;
      until = copy.until;
    }

    public Builder id(RegistrationInvitationId id) {
      this.id = id;
      return this;
    }

    public Builder tenantId(TenantId tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder invitationToken(String invitationToken) {
      this.invitationToken = invitationToken;
      return this;
    }

    public Builder startingOn(Long startingOn) {
      this.startingOn = startingOn;
      return this;
    }

    public Builder create() {
      isCreate = true;
      return this;
    }

    public Builder until(Long until) {
      this.until = until;
      return this;
    }

    public RegistrationInvitation build() {
      Verify.isNotNull(id, "Id is required, even for create.");
      Verify.isNotNull(tenantId, "The tenantId is required.");
      isNotEmpty(invitationToken, "The invitation token is required.");
      hasLength(invitationToken, 1, 36, "The invitation token must be 36 characters or less.");
      Verify.isNotNull(startingOn, "The 'starting on' date is required.");
      Verify.isNotNull(until, "The 'until' date is required.");
      Verify.isTrue(startingOn < this.until, IllegalStateException.class, "Start date must come before end date.");

      if (isCreate) {
        Verify.isTrue(System.currentTimeMillis() < until, IllegalStateException.class, "End date must be in the future.");
      }
      Verify.isNotNull(id, "Id must be set when create is not specified");
      return new RegistrationInvitation(this);
    }
  }
}
 
