package com.comcast.rally.pec.modules

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.comcast.rally.pec.modules.ActorMaterializerModule.ActorMaterializerProvider
import com.google.inject.{ AbstractModule, Inject, Provider }
import net.codingwell.scalaguice.ScalaModule

object ActorMaterializerModule {
  class ActorMaterializerProvider @Inject() (actorSystem: ActorSystem) extends Provider[ActorMaterializer] {
    implicit val system = actorSystem
    override def get(): ActorMaterializer = {
      ActorMaterializer()
    }
  }
}
class ActorMaterializerModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = bind[ActorMaterializer].toProvider[ActorMaterializerProvider].asEagerSingleton()
}
