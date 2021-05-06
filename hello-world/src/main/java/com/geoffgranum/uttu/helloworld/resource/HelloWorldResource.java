/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.geoffgranum.uttu.helloworld.resource;

import com.geoffgranum.uttu.helloworld.domain.ServerPerformanceInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author ggranum
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface HelloWorldResource {

  @GET()
  @Path("/hello/{whatever}")
  @Produces(MediaType.TEXT_PLAIN)
  String hello(@PathParam("whatever") String whatever);

  @GET()
  @Path("/hello-query")
  @Produces(MediaType.TEXT_PLAIN)
  String helloQuery(@QueryParam("whatever") String whatever);

  @GET()
  @Path("/version")
  @Produces(MediaType.TEXT_PLAIN)
  String version();

  @GET()
  @Path("/performance/{anyValue}")
  ServerPerformanceInfo perfCheck(@PathParam("anyValue") String anyValue);
}


