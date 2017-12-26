package com.comcast.rally.pec.routes

import javax.ws.rs.Path

import akka.http.scaladsl.server.{ Directives, Route }
import io.swagger.annotations._

/**
 * Created by jshah002c on 6/30/17.
 */
@Path("calls")
@Api(value = "calls")
trait Calls extends Directives {

  @ApiOperation(value = "Return Hello greeting", notes = "", nickname = "anonymousHello", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "petId", required = false, dataType = "integer", paramType = "path", value = "ID of pet that needs to be fetched")))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 400, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 401, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 403, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")))
  def getRoute =
    path("user/calls") { // Listens to paths that are exactly `/calls`
      get {
        // Listens only to GET requests
        complete("Say hello to akka-http") // Completes with some html page
      }
    }
  @ApiOperation(value = "Return Hello greeting", notes = "", nickname = "anonymousHello", httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 400, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 401, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 403, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")))
  def putRoute =
    path("calls") {
      put {
        complete("Say hello to akka-http")
      }
    }

  @ApiOperation(value = "Return Hello greeting", notes = "", nickname = "anonymousHello", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 400, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 401, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 403, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")))
  def postRoute: Route =
    path("calls") {
      post {
        complete("Say hello to akka-http")
      }
    }

  @ApiOperation(value = "Return Hello greeting", notes = "", nickname = "anonymousHello", httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 400, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 401, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 403, message = "Return Hello Greeting", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")))
  def deleteRoute: Route =
    path("calls") {
      delete {
        complete { ("Say hello to akka-http") }
      }
    }

  val callRoutes = getRoute ~ putRoute ~ postRoute ~ deleteRoute

}