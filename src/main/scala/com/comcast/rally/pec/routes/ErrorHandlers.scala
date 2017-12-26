package com.comcast.rally.pec.routes

import java.io.IOException

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, ExceptionHandler }

object ErrorHandlers extends Directives {

  val INVALID_USER_ID = "invalid login id"
  val INVALID_PASSWORD = "invalid password"
  val NOT_FOUND = "not found"
  val NOT_EXIST = "not exist"
  val CANNOT_BE_FOUND = "cannot be found"
  val ALREADY_IN_USE = "already in use"
  val NOT_AVAILABLE = "not available"
  val INVALID = "invalid"
  val SHOULD_BE_CHOSEN = "should be chosen"
  val MUST_BE_AFTER = "must be after"
  val ALREADY_EXISTS = "already exists"

  val PREDICATE_404: String => Boolean = (messageString: String) => {
    messageString.contains(NOT_FOUND) ||
      messageString.contains(NOT_EXIST) ||
      messageString.contains(NOT_AVAILABLE) ||
      messageString.contains(CANNOT_BE_FOUND)
  }

  val PREDICATE_400: String => Boolean = (messageString: String) => {
    messageString.contains(SHOULD_BE_CHOSEN) ||
      messageString.contains(ALREADY_EXISTS) ||
      messageString.contains(ALREADY_IN_USE) ||
      messageString.contains(MUST_BE_AFTER) ||
      messageString.contains(INVALID)
  }

  val PREDICATE_401: String => Boolean = (messageString: String) => {
    messageString.contains(INVALID_PASSWORD) || messageString.contains(INVALID_USER_ID)
  }

  val exceptionHandler = ExceptionHandler {
    case e: NoSuchElementException => {
      complete(StatusCodes.NotFound -> e.getMessage)
    }
    case e: IOException => {
      complete(StatusCodes.InternalServerError -> e.getMessage)
    }

    case throwable: Throwable => complete(StatusCodes.InternalServerError -> throwable.getMessage)
  }
}