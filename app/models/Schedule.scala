package models

class Schedule {

  // { carId => { departureTime, CarStop } }
  val schedule: Map[Int, Map[String, CarStop]] = Map[Int, Map[String, CarStop]]()

  class CarStop(carId: Int, maxCapacity: Int, time: String, departure: String, destination: String) {

  }

  def addCarStop(carId: Int, maxCapacity: Int, time: String, departure: String, destination: String): Unit = {

    val stop = new CarStop(carId, maxCapacity, time, departure, destination)
    // ToDo: add this to schedule data structure

  }

}
