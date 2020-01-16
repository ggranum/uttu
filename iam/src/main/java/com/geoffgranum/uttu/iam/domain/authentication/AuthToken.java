/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.geoffgranum.uttu.iam.domain.authentication;


public interface AuthToken {

  TenantId tenantId();

  String username();

  String password();
}
