package com.comcast.rally.pec

import javax.inject.{ Inject, Singleton }

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.comcast.rally.pec.modules._
import com.comcast.rally.pec.routes.{ BaseRoutes, CorsSupport, HealthCheck, Services }
import com.github.racc.tscg.{ TypesafeConfig, TypesafeConfigModule }
import com.google.inject.Guice
import com.typesafe.config.{ Config, ConfigFactory }
import com.typesafe.scalalogging.Logger

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.util.{ Failure, Success }

/**
 *
 * Created by jshah002c on 7/3/17.
 */
object RallyHttpServer extends App {

  val injector = Guice.createInjector(
    TypesafeConfigModule.fromConfigWithPackage(ConfigFactory.load(), "com.comcast.rally.pec"),
    new ActorSystemModule,
    new ActorMaterializerModule)

  val ociApi = injector.getInstance(classOf[OciApi])

  // pre start cleanup
  import ociApi.system.dispatcher
  sys.addShutdownHook {
    ociApi.httpBinding.flatMap(_.unbind()).onComplete {
      _ =>
        ociApi.materializer.shutdown()
        ociApi.system.terminate()
        Await.result(ociApi.system.whenTerminated, 60 seconds)
    }
  }
}

@Singleton
class OciApi @Inject() (
  @TypesafeConfig("rally-pec") configuration: Config,
  actorSystem: ActorSystem,
  actorMaterializer: ActorMaterializer,
  servicesResource: Services,
  swaggerDocService: SwaggerDocService,
  healthCheck: HealthCheck) extends Directives with CorsSupport {
  import de.heikoseeberger.accessus.Accessus._
  implicit val system: ActorSystem = actorSystem
  implicit val executor = system.dispatcher
  implicit val materializer = actorMaterializer
  val logger = Logging(system, getClass)
  private val config = configuration
  //private val routes = BaseRoutes.baseRoutes ~ healthCheck.healthCheck ~ servicesResource.serviceRoute
  private val routes = BaseRoutes.baseRoutes ~ healthCheck.healthCheck ~ servicesResource.serviceRoute ~ swaggerDocService.swaggerRoute
  val httpBinding = Http().bindAndHandle(
    corsHandler(routes).withAccessLog(accessLog(Logger("ACCESS-LOG"))),
    config.getString("http.interface"),
    config.getInt("http.port"))

  httpBinding.onComplete {
    case Success(serverBinding) => logger.info(s"service started at ${serverBinding.localAddress}")
    case Failure(error) => logger.error(error, "error during startup")
  }

  def accessLog(log: Logger): AccessLog[Long, Future[Done]] =
    Sink.foreach {
      case ((req, t0), res) =>
        val m = req.method.value
        val p = req.uri.path.toString
        val s = res.status.intValue()
        val t = (now() - t0) / 1000
        log.info(s"$m $p status=$s responseTime=$t")
    }

  private def now() = System.nanoTime()
}