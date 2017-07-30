package models


import java.io.File
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Simulator {

  val stations_dir = "/Users/jorgegrajales/Repos/transmi-metro/data/stations"

  def getStationList(): List[String] = {
    def getListOfFiles(dir: String): List[File] = {
      val d = new File(dir)
      if (d.exists && d.isDirectory) {
        (d.listFiles.filter(_.isFile)).toList
      } else {
        List[File]()
      }
    }

    val stations_files = getListOfFiles(stations_dir).map(x => x.toString)
      .map(x => x.substring(0, x.lastIndexOf('.'))).map(x => x.substring(x.lastIndexOf("/") + 1))
    return stations_files
  }

  def getPassengers(station: String): List[Passenger] = {
    val rows = ArrayBuffer[Array[String]]()
    var passengers = List[Passenger]()
    using(Source.fromFile(stations_dir + "/" + station + ".csv")) { source =>
      for (line <- source.getLines.drop(1)) {
        rows += line.split(",").map(_.trim)
      }
    }

    for (row <- rows) {
      val passengerId = row(0).toInt
      val time = row(1)
      val destination = row(2)
      passengers :+ new Passenger(passengerId, destination)
    }

    def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
      try {
        f(resource)
      } finally {
        resource.close()
      }

    return passengers
  }


}