package com.comcast.rally.pec.routes

import akka.http.scaladsl.server.{ Directives, Route }

/**
 * Created by jshah002c on 6/30/17.
 */
object BaseRoutes extends Directives {

  val baseRoutes: Route = pathEndOrSingleSlash {
    complete("server is up and running")
  } ~ (get & path("about")) {
    complete("done")
  }
}