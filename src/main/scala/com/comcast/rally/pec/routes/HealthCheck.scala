package com.comcast.rally.pec.routes

import javax.inject.Singleton
import javax.ws.rs.Path

import akka.http.scaladsl.server.{ Directives, Route }
import io.swagger.annotations._

@Api(value = "Health Check")
@Path("/health")
@Singleton
class HealthCheck extends Directives {

  @Path("/ping")
  @ApiOperation(value = "health check endpoint returning pong for ping", httpMethod = "GET", produces = "text/plain")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "pong", response = classOf[String]),
    new ApiResponse(code = 404, message = "Not Found")))
  def pingpong: Route = get {
    (path("health" / "ping") & pathEndOrSingleSlash) {
      complete("pong")
    }
  }
  private val healthCheckEndpoint = logRequestResult("user-services-healthCheck") {
    pingpong
  }

  val healthCheck: Route = pingpong
}