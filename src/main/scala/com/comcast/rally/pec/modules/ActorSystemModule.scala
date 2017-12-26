package com.comcast.rally.pec.modules

import javax.inject.{ Inject, Provider }

import akka.actor.ActorSystem
import com.comcast.rally.pec.modules.ActorSystemModule.ActorSystemProvider
import com.github.racc.tscg.TypesafeConfig
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule

object ActorSystemModule {
  class ActorSystemProvider @Inject() (@TypesafeConfig("akka") config: Config) extends Provider[ActorSystem] {
    override def get(): ActorSystem = {
      ActorSystem("rally-pec-system", config)
    }
  }
}

class ActorSystemModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = bind[ActorSystem].toProvider[ActorSystemProvider].asEagerSingleton()
}
