package models

import scala.collection.mutable.Map

class Schedule {

  // { carId => { departureTime, CarStop } }
  val schedule: Map[Int, Map[String, CarStop]] =
    Map[Int, Map[String, CarStop]]().withDefaultValue(Map[String, CarStop]())

  def addCarStop(carId: Int, maxCapacity: Int, time: String, departure: String, destination: String): Unit = {
    val stop = new CarStop(carId, maxCapacity, time, departure, destination)
    schedule(carId).put(time, stop)
  }

  def getCarStop(car: Car, time: String): CarStop = {
    schedule(car.id)(time)
  }

}

class CarStop(var carId: Int, var maxCapacity: Int, var time: String, var departure: String, var destination: String) {
}
