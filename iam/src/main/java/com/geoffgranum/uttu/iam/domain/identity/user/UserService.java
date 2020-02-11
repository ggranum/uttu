package com.geoffgranum.uttu.iam.domain.identity.user;

import com.geoffgranum.uttu.core.base.Verify;
import com.geoffgranum.uttu.iam.domain.identity.Enablement;
import com.geoffgranum.uttu.iam.domain.identity.EncryptionToken;
import com.geoffgranum.uttu.iam.domain.identity.user.event.UserEnablementChanged;
import com.geoffgranum.uttu.iam.domain.identity.user.event.UserPasswordChanged;
import com.google.common.eventbus.EventBus;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

import static com.geoffgranum.uttu.core.base.Verify.isNotEmpty;

@Immutable
public final class UserService {

  private final EventBus domainBus;

  @Inject
  private UserService(EventBus domainBus) {
    this.domainBus = domainBus;
  }

  public User changePassword(User user, String currentPasswordClearText, String newPasswordClearText) {
    isNotEmpty(currentPasswordClearText, "Current password is required.");
    isNotEmpty(newPasswordClearText, "New password is required.");
    Verify.equal(user.passwordHash,
        EncryptionToken.fromPasswordClearText(currentPasswordClearText, user.saltHex).hashAsHexString,
        "Passwords do not match.");

    User newUser = new User.Builder()
        .copyOf(user)
        .passwordClearText(newPasswordClearText)
        .build();
    domainBus.post(new UserPasswordChanged(newUser.tenantId, newUser.username));
    return user;
  }

  public User defineEnablement(User forUser, Enablement enablement) {
    Verify.isNotNull(forUser, "The User is required.");
    Verify.isNotNull(enablement, "The enablement is required.");

    User user = forUser.copy().enablement(enablement).build();

    domainBus.post(new UserEnablementChanged(user.tenantId, user.username, user.enablement));
    return user;
  }

}
