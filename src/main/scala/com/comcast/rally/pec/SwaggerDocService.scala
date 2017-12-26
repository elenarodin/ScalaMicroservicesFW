package com.comcast.rally.pec

import javax.inject.{ Inject, Singleton }

import akka.http.scaladsl.model.StatusCodes
import com.comcast.rally.pec.routes.{ HealthCheck, Services }
import com.github.racc.tscg.TypesafeConfig
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import io.swagger.models.ExternalDocs
import io.swagger.models.auth.ApiKeyAuthDefinition

@Singleton
class SwaggerDocService @Inject() (@TypesafeConfig("rally-pec.http.interface") hostname: String, @TypesafeConfig("rally-pec.http.port") port: Int) extends SwaggerHttpService {

  override val apiClasses: Set[Class[_]] = Set(classOf[Services], classOf[HealthCheck])
  //  override val host = s"${InetAddress.getLocalHost.getHostName}:$port"
  override val host = s"$hostname:$port"
  override val info = Info(version = "1.0")
  override val externalDocs = Some(new ExternalDocs("Core Docs", "http://acme.com/docs"))
  override val securitySchemeDefinitions = Map("basicAuth" -> new ApiKeyAuthDefinition())
  val swaggerRoute =
    pathPrefix(apiDocsPath) {
      pathSingleSlash(get(redirect("ui", StatusCodes.PermanentRedirect))) ~
        pathPrefix("ui") { getFromResource("swagger/index.html") }
    } ~
      pathPrefix(apiDocsPath / Segment) { resourceName =>
        getFromResource(s"swagger/$resourceName")
      } ~
      routes
}