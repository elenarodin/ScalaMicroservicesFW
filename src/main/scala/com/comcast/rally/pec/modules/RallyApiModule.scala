package com.comcast.rally.pec.modules

import java.net.URI
import javax.inject.Inject

import com.comcast.rally.pec.modules.RallyApiModule.RallyApiProvider
import com.github.racc.tscg.TypesafeConfig
import com.google.inject.{ AbstractModule, Provider }
import com.rallydev.rest.RallyRestApi
import net.codingwell.scalaguice.ScalaModule

object RallyApiModule {
  class RallyApiProvider @Inject() (@TypesafeConfig("rally-pec.url") url: String) extends Provider[RallyRestApi] {
    override def get(): RallyRestApi = new RallyRestApi(new URI(url), "")
  }
}
class RallyApiModule extends AbstractModule with ScalaModule {
  override def configure() = bind[RallyRestApi].toProvider[RallyApiProvider].asEagerSingleton()

  //  rallyInstance.setWsapiVersion(new String("1.42"))
  //  rallyInstance.setApplicationName("TeamCity to Rally Integrator")
  //  rallyInstance.setApplicationVendor("OpenSource")
  //  rallyInstance.setApplicationVersion("1.0")
}
