package com.comcast.rally.pec

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.AbstractMatcherFilter
import ch.qos.logback.core.sift.AbstractDiscriminator
import ch.qos.logback.core.spi.FilterReply

class AccessLogDiscriminator extends AbstractDiscriminator[ILoggingEvent] {
  override def getDiscriminatingValue(e: ILoggingEvent) = "ACCESS-LOG"
  override def getKey = "ACCESS-LOG"
}

class AccesLogFilter extends AbstractMatcherFilter[ILoggingEvent] {
  private val AccessLog = "ACCESS-LOG"
  override def decide(event: ILoggingEvent) =
    if (event.getLoggerName.startsWith(AccessLog)) FilterReply.ACCEPT
    else FilterReply.DENY

}

