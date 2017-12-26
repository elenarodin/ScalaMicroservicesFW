package com.comcast.rally.pec.service

import java.net.URI
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Singleton

import com.github.racc.tscg.TypesafeConfig
import com.google.inject.Inject
import com.rallydev.rest.RallyRestApi
import com.rallydev.rest.request.QueryRequest
import com.rallydev.rest.util.{ Fetch, QueryFilter }
import com.typesafe.scalalogging.StrictLogging

@Singleton
class RallyService @Inject() (@TypesafeConfig("rally-pec.url") url: String) extends StrictLogging {

  var numberOfReleases = 0

  def proceed(releaseNames: String, rallyAPIKey: String): Map[String, String] = {
    println("итак, начнем")
    println("")

    val rallyRestApi = new RallyRestApi(new URI(url), rallyAPIKey)
    val arrReleases: List[String] = releaseNames.split(",").toList
    numberOfReleases = arrReleases.size

    var jsonStr = ""
    var myMap = Map[String, String]()

    var i = 0
    while (i < numberOfReleases) {

      println(arrReleases(i))
      jsonStr = getFeaturesByRelease(rallyRestApi, arrReleases(i), i)

      myMap += (arrReleases(i) -> jsonStr)
      println(myMap.get(arrReleases(i)))

      i += 1
    }
    rallyRestApi.close()

    myMap
  }

  def getFeaturesByRelease(rallyRestApi: RallyRestApi, release: String, nRelease: Integer): String = {
    val feature = new QueryRequest("portfolioItem/feature")

    feature.setQueryFilter(new QueryFilter("Release.Name", "=", release))
    feature.setFetch(new Fetch("Name", "Description", "Release", "Attachments"))

    val featureQueryResponse = rallyRestApi.query(feature)
    if (featureQueryResponse.getResults.size == 0) return "There is nothing found for " + release + " Release."

    //temp string for flatFile
    var namesStr = ""

    //initializing json string
    var str = ""
    str = "[".concat("{\"CurrentDate\":").concat("\"").concat(getCurrentDate()).concat("\"},")

    //adding features to json string
    str = str.concat("{").concat("\"").concat(release).concat("\"").concat(": [")

    var j = 0
    while ({
      j < featureQueryResponse.getResults.size
    })

      if (featureQueryResponse.getResults.get(j).isJsonObject) {

        str = str.concat(featureQueryResponse.getResults.get(j).getAsJsonObject.toString)

        //not last feature
        if (j < featureQueryResponse.getResults.size - 1) {
          str = str.concat(",")

          // last feature and not last release
        } else if (j == featureQueryResponse.getResults.size - 1 && nRelease + 1 < numberOfReleases) {
          str = str.concat("] } ,")

          // last feature and last release
        } else if (j == featureQueryResponse.getResults.size - 1 && nRelease + 1 == numberOfReleases) {
          str = str.concat("] } ")
        }

        //temp for features list flat file
        println(featureQueryResponse.getResults.get(j).getAsJsonObject.get("Name"))
        namesStr = namesStr.concat("\n" + j.toString + ". " + featureQueryResponse.getResults.get(j).getAsJsonObject.get("Name").toString)

        j += 1;

      }
    str
  }
  def getCurrentDate(): String = {

    val format = new SimpleDateFormat("d-M-y")
    val now = format.format(Calendar.getInstance().getTime)

    return now.toString
  }

}
