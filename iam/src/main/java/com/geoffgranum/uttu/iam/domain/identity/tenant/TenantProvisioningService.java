/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.geoffgranum.uttu.iam.domain.identity.tenant;

import com.geoffgranum.uttu.core.persistence.id.IdGenerator;
import com.geoffgranum.uttu.iam.domain.access.registration.RegistrationInvitation;
import com.geoffgranum.uttu.iam.domain.access.role.Role;
import com.geoffgranum.uttu.iam.domain.access.role.RoleRepository;
import com.geoffgranum.uttu.iam.domain.access.role.RoleService;
import com.geoffgranum.uttu.iam.domain.identity.Enablement;
import com.geoffgranum.uttu.iam.domain.identity.tenant.event.TenantAdministratorRegistered;
import com.geoffgranum.uttu.iam.domain.identity.tenant.event.TenantProvisioned;
import com.geoffgranum.uttu.iam.domain.identity.user.FullName;
import com.geoffgranum.uttu.iam.domain.identity.user.User;
import com.geoffgranum.uttu.iam.domain.identity.user.UserRepository;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Geoff M. Granum
 */
public class TenantProvisioningService {

  private final IdGenerator idGen;
  private final RoleRepository roleRepository;
  private final TenantRepository tenantRepository;
  private final UserRepository userRepository;
  private final Provider<Tenant.Builder> tenantFactory;
  private final TenantService tenantService;
  private final RoleService roleService;
  private final EventBus domainBus;

  @Inject
  protected TenantProvisioningService(
      EventBus domainBus,
      TenantRepository tenantRepository,
      UserRepository userRepository,
      RoleRepository roleRepository,
      Provider<Tenant.Builder> tenantFactory,
      TenantService tenantService,
      IdGenerator idGen, RoleService roleService) {

    super();
    this.domainBus = domainBus;
    this.roleRepository = roleRepository;
    this.tenantRepository = tenantRepository;
    this.userRepository = userRepository;
    this.tenantFactory = tenantFactory;
    this.tenantService = tenantService;
    this.idGen = idGen;
    this.roleService = roleService;
  }

  public Tenant provisionTenant(
      String tenantName,
      String tenantDescription,
      String tenantServerHostname,
      FullName administratorName,
      String administratorPasswordClearText,
      String emailAddress,
      String primaryTelephone,
      String secondaryTelephone,
      boolean asSystemTenant) {

    // must be active to register admin
    Tenant tenant = tenantFactory.get()
        .systemTenant(asSystemTenant)
        .name(tenantName)
        .description(tenantDescription)
        .serverHostname(tenantServerHostname)
        .active(true)
        .build(new TenantId(idGen.next()));

    this.tenantRepository.add(tenant);

    this.registerAdministratorFor(
        tenant,
        administratorName,
        administratorPasswordClearText,
        emailAddress,
        primaryTelephone,
        secondaryTelephone,
        asSystemTenant);

    domainBus.post(new TenantProvisioned(tenant.id()));

    return tenant;
  }

  private void registerAdministratorFor(
      Tenant tenant,
      FullName administratorName,
      String administratorPasswordClearText,
      String emailAddress,
      String primaryTelephone,
      String secondaryTelephone,
      boolean asSystemTenant) {

    RegistrationInvitation invitation = tenant.offerRegistrationInvitation("init").openEnded().build();

    User admin = tenantService.registerUser(tenant,
        invitation.invitationToken,
        emailAddress,
        administratorPasswordClearText,
        Enablement.indefiniteEnablement()
    );

    tenantService.withdrawInvitation(tenant, invitation.invitationToken);
    this.userRepository.add(tenant.id, admin);

    Role adminRole;

    if (asSystemTenant) {
      adminRole = tenantService.provisionRole(
          tenant,
          "System  Administrator",
          "The default system-wide administrator account. Root.",
          Role.DEFAULT_SYSTEM_ADMIN_PERMISSIONS);
    } else {
      adminRole = tenantService.provisionRole(
          tenant,
          "Administrator",
          "The default administrator user for this account.",
          Role.DEFAULT_TENANT_ADMIN_PERMISSIONS);
    }
    roleService.assignUser(adminRole, admin);
    this.roleRepository.add(adminRole);
    domainBus.post(
        new TenantAdministratorRegistered(
            tenant.id(),
            tenant.name,
            administratorName,
            emailAddress,
            admin.username)
    );
  }
}
 
