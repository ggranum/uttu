/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.tenant;


import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.core.persistence.id.IdGenerator;
import com.geoffgranum.uttu.core.persistence.id.Identified;
import com.geoffgranum.uttu.iam.domain.access.registration.InvitationDescriptor;
import com.geoffgranum.uttu.iam.domain.access.registration.RegistrationInvitation;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.time.DateUtils;

import javax.annotation.Nullable;
import java.util.*;

import static com.geoffgranum.uttu.core.base.Verify.*;

public final class Tenant implements Identified {

  public final TenantId id;
  public final Boolean systemTenant;
  public final String name;
  public final String description;
  public final String serverHostname;
  public final boolean active;

  public final Set<RegistrationInvitation> registrationInvitations;

  private Tenant(Builder builder) {
    id = builder.id;
    systemTenant = builder.systemTenant;
    name = builder.name;
    description = builder.description;
    serverHostname = builder.serverHostname;
    active = builder.active;
    this.registrationInvitations = builder.registrationInvitations;
  }

  @Override
  public TenantId id() {
    return id;
  }


  public Collection<InvitationDescriptor> allAvailableRegistrationInvitations() {
    isTrue(active, "Tenant is not active.");
    return this.allRegistrationInvitationsFor(true);
  }

  public Collection<InvitationDescriptor> allUnavailableRegistrationInvitations() {
    isTrue(active, "Tenant is not active.");

    return this.allRegistrationInvitationsFor(false);
  }

  public boolean isRegistrationAvailableThrough(String invitationIdentifier) {
    isTrue(active, "Tenant is not active.");
    RegistrationInvitation invitation = this.invitation(invitationIdentifier);
    return invitation != null && invitation.isAvailable();
  }

  /**
   * Create a registration invitation that is good "forever"
   */
  public RegistrationInvitation offerOpenEndedRegistrationInvitation(String description) {
    return offerRegistrationInvitation(description, System.currentTimeMillis(), Long.MAX_VALUE);
  }

  /**
   * Create a registration invitation that is good for 30 days.
   */
  public RegistrationInvitation offerRegistrationInvitation(String description) {
    return offerRegistrationInvitation(description,
        System.currentTimeMillis(),
        DateUtils.addDays(new Date(), 30).getTime());
  }

  /**
   * Create a registration invitation that is good for the specified time frame.
   */
  public RegistrationInvitation offerRegistrationInvitation(String description, Long starting, Long ending) {
    Verify.isTrue(active, IllegalStateException.class, "Tenant is not active.");
    Verify.isNotNull(starting, "Start date is required.");
    Verify.isNotNull(ending, "End date is required.");
    isTrue(starting < ending, "Start date must come before end date.");
    isTrue(System.currentTimeMillis() < ending, "End date must be in the future.");
    isFalse(
        this.isRegistrationAvailableThrough(description),
        IllegalStateException.class,
        "Invitation already exists.");

    RegistrationInvitation invitation =
        new RegistrationInvitation.Builder()
            .tenantId(id)
            .invitationToken(UUID.randomUUID().toString().toUpperCase())
            .description(description)
            .startingOn(starting)
            .until(ending)
            .create()
            .build();

    boolean added = this.registrationInvitations.add(invitation);

    Verify.isTrue(added, IllegalStateException.class, "The invitation should have been added.");

    return invitation;
  }


  protected Collection<InvitationDescriptor> allRegistrationInvitationsFor(boolean isAvailable) {
    Set<InvitationDescriptor> allInvitations = new HashSet<>();

    for (RegistrationInvitation invitation : this.registrationInvitations()) {
      if (invitation.isAvailable() == isAvailable) {
        allInvitations.add(invitation.toDescriptor());
      }
    }

    return Collections.unmodifiableSet(allInvitations);
  }

  @Nullable
  protected RegistrationInvitation invitation(String invitationIdentifier) {
    RegistrationInvitation invite = null;
    for (RegistrationInvitation invitation : this.registrationInvitations()) {
      if (invitation.isIdentifiedBy(invitationIdentifier)) {
        invite = invitation;
        break;
      }
    }
    return invite;
  }

  Set<RegistrationInvitation> registrationInvitations() {
    return Collections.unmodifiableSet(this.registrationInvitations);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Tenant other = (Tenant) obj;
    return Objects.equal(this.id, other.id);
  }

  public Builder copy() {
    return new Builder().copyOf(this);
  }

  //  public DBObject toMongo() {
  //    BasicDBObject mongo = new BasicDBObject();
  //    mongo.append("_id", id.value());
  //    mongo.append("systemTenant", systemTenant);
  //    mongo.append("name", name);
  //    mongo.append("description", description);
  //    mongo.append("serverHostname", serverHostname);
  //    mongo.append("active", active);
  //    BasicDBList registrationInvitations = new BasicDBList();
  //    for (RegistrationInvitation item : this.registrationInvitations) {
  //      registrationInvitations.add(item.toMongo());
  //    }
  //    mongo.append("registrationInvitations", registrationInvitations);
  //    return mongo;
  //  }

  public static final class Builder {

    private TenantId id;
    private String name;
    private String description;
    private String serverHostname;
    private Boolean active = true;
    private Boolean systemTenant = false;
    private Set<RegistrationInvitation> registrationInvitations = Sets.newHashSet();


    public Builder() {
    }

    //    public Builder from(BasicDBObject mongo) {
    //      this.id(new TenantId(mongo.getObjectId("_id")));
    //      this.systemTenant(mongo.getBoolean("systemTenant"));
    //      this.name(mongo.getString("name"));
    //      this.description(mongo.getString("description"));
    //      this.serverHostname(mongo.getString("serverHostname"));
    //      this.active(mongo.getBoolean("active"));
    //      Set<RegistrationInvitation> newRegistrationInvitationItems = Sets.newHashSet();
    //      BasicDBList list = (BasicDBList)mongo.get("registrationInvitations");
    //      for (Object item : list) {
    //        newRegistrationInvitationItems.add(new RegistrationInvitation.Builder().from((BasicDBObject)item).build());
    //      }
    //      this.registrationInvitations(newRegistrationInvitationItems);
    //      return this;
    //    }

    public Builder copyOf(Tenant copy) {
      id = copy.id;
      systemTenant = copy.systemTenant;
      name = copy.name;
      description = copy.description;
      serverHostname = copy.serverHostname;
      active = copy.active;
      registrationInvitations = copy.registrationInvitations;
      return this;
    }

    public Builder systemTenant(boolean isSystemTenant) {
      this.systemTenant = isSystemTenant;
      return this;
    }

    public Builder id(TenantId id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder serverHostname(String serverHostname) {
      this.serverHostname = serverHostname;
      return this;
    }

    public Builder active(boolean active) {
      this.active = active;
      return this;
    }

    public Builder registrationInvitations(Set<RegistrationInvitation> registrationInvitations) {
      this.registrationInvitations = registrationInvitations;
      return this;
    }

    public Tenant create(IdGenerator gen) {
      id(new TenantId(gen.nextId()));
      return this.build();
    }

    public Tenant build() {
      isNotNull(id, "Id is required.");
      isNotEmpty(name, "Tenant name is required.");
      hasLength(name, 0, 100, "Tenant name must be 100 characters or less.");
      Verify.isNotNull(id, "Id must be set unless create is specified");
      return new Tenant(this);
    }
  }
}
 
