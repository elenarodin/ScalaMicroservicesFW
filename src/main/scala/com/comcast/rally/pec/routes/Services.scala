package com.comcast.rally.pec.routes

import javax.inject.{ Inject, Singleton }
import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{ HttpHeader, StatusCodes }
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.comcast.rally.pec.service.RallyService
import io.swagger.annotations._
import spray.json.{ DefaultJsonProtocol, RootJsonFormat }

/**
 * Created by jshah002c on 6/30/17.
 */
@Api(value = "Release Details")
@Path("/project/releases")
@Singleton
class Services @Inject() (system: ActorSystem, materializer: ActorMaterializer, rallyService: RallyService) extends Directives {
  val logger: LoggingAdapter = Logging(system, getClass)
  import CustomJsonFormats._
  private val pathMatcherForServicePacks: PathMatcher[Tuple1[String]] = "project" / "releases" / Segment / "details"
  private val pathMatcherForServiceAssignments: PathMatcher[Tuple1[String]] = "project" / "releases" / Segment / "serviceAssignments"

  private val RallyAPIKey = "Rally-API-Key"
  private def extractRallyAPIKey: HttpHeader => Option[String] = {
    case HttpHeader(`RallyAPIKey`, value) => {
      Some(value)
    }
    case _ => None
  }
  @Path("/{releaseName}/details")
  @ApiOperation(value = "returns features for the releases", httpMethod = "GET", produces = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "releaseName", value = "name of the release", required = false, dataType = "string", paramType = "path"),
    new ApiImplicitParam(name = "Rally-API-Key", required = true, dataTypeClass = classOf[String], paramType = "header")))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "List of Features for the Release"),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 400, message = "Bad Request"),
    new ApiResponse(code = 401, message = "Unauthorized"),
    new ApiResponse(code = 403, message = "Forbidden")))
  def servicesForUser: Route =
    get {
      (path(pathMatcherForServicePacks) & optionalHeaderValueByName(RallyAPIKey) & pathEndOrSingleSlash) { (releaseName, rallyAPIKey) =>
        rallyAPIKey match {
          case Some(apiKey) => complete(ToResponseMarshallable(CustomMap(rallyService.proceed(releaseName, apiKey))))
          case None => complete(StatusCodes.BadRequest -> "Api Key Required, Please obtain the rally api key from: ")
        }
      }
    }

  @Path("/{releaseName}/serviceassignments")
  @ApiOperation(value = "returns requested assigned services", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "userId", value = "user identification", required = true, dataType = "string", paramType = "path")))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Service assignments", response = classOf[CustomMap]),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 400, message = "Bad Request"),
    new ApiResponse(code = 401, message = "Unauthorized"),
    new ApiResponse(code = 403, message = "Forbidden")))
  def serviceAssignmentForUser: Route = {
    get {
      (path(pathMatcherForServicePacks) & optionalHeaderValueByName(RallyAPIKey) & pathEndOrSingleSlash) { (releaseName, cimaguid) =>
        //        entity(as[ExternalServer]) { externalServer =>
        //          onSuccess(ociService.getUserServiceAssignments(UserId(userId), CustomerGuid(cimaguid.getOrElse("")), externalServer)) { resultMap =>
        complete(ToResponseMarshallable(CustomMap(Map.empty)))
        //          }
        //        }
      }
    }
  }

  private val userBased: Route =
    logRequestResult("user-services") {
      servicesForUser ~
        serviceAssignmentForUser
    }

  val serviceRoute = handleExceptions(ErrorHandlers.exceptionHandler) {
    userBased
  }
}

case class CustomMap(listOfKeyValues: Map[String, String] = Map[String, String]())
object CustomJsonFormats extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val customMapJsonFormat: RootJsonFormat[CustomMap] = jsonFormat1(CustomMap.apply)
}
