package models

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Schedule {

  // { carId => { departureTime, CarStop } }
  val schedule: Map[Int, Map[String, CarStop]] = Map[Int, Map[String, CarStop]]()

  class CarStop(carId: Int, maxCapacity: Int, time: String, departure: String, destination: String) {

  }

  def addCarStop(carId: Int, maxCapacity: Int, time: String, departure: String, destination: String): Unit = {

    val stop = new CarStop(carId, maxCapacity, time, departure, destination)
    // ToDo: add this to schedule data structure

  }

  def readSchedule() {

    val rows = ArrayBuffer[Array[String]]()
    using(Source.fromFile("/Users/jorgegrajales/Repos/transmi-metro/data/schedules.csv")) { source =>
      for (line <- source.getLines.drop(1)) {
        rows += line.split(",").map(_.trim)
      }
    }
    for (row <- rows) {
      var carId = row(0).toInt
      var maxCapacity = row(1).toInt
      var time = row(3)
      var departure = row(4)
      var destination = row(1)
      addCarStop(carId, maxCapacity, time, departure, destination)
    }

    def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
      try {
        f(resource)
      } finally {
        resource.close()
      }

  }
}
