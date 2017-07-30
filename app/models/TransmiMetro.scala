package models

import scala.collection.mutable

object TransmiMetro {
  val DATA_PATH: String = "./data/"
}

class TransmiMetro {

  // { time => { station, [cars] } }
  val cars: mutable.Map[String, mutable.Map[String, mutable.Seq[Car]]] =
    loadCars()

  // { time => { station => [passengers] } }
  val passengers: mutable.Map[String, mutable.Map[String, mutable.Seq[Passenger]]] =
    loadPassengers()

  // { time => { station => { arrivals: n, departures: m } } }
  val log: mutable.Map[String, mutable.Map[String, mutable.Map[String, Int]]] =
    mutable.Map[String, mutable.Map[String, mutable.Map[String, Int]]]()

  // list of available stations
  val stations: Seq[Station] =
    Seq[Station](
      new Station("Portal Americas", true, this),
      new Station("Calle 42 sur", false, this),
      new Station("Carrera 80", false, this),
      new Station("Kennedy", false, this),
      new Station("Avenida Boyaca", false, this),
      new Station("Carrera 68", false, this),
      new Station("Carrera 50", false, this),
      new Station("NQS", false, this),
      new Station("Narino", false, this),
      new Station("Calle 1", true, this),
      new Station("Calle 10", false, this),
      new Station("Calle 26", false, this),
      new Station("Calle 45", false, this),
      new Station("Calle 63", false, this),
      new Station("Calle 72", true, this),
    )

  def log(time: String, station: String, stat: String, num: Int): Unit = {
    if (!log.contains(time))
      log.put(time, mutable.Map[String, mutable.Map[String, Int]]())
    if (!log(time).contains(station))
      log(time).put(station, mutable.Map("arrivals" -> 0, "departures" -> 0))
    val current = log(time)(station)(stat)
    log(time)(station).put(stat, current + num)
  }

  // private helpers

  private def loadCars(): mutable.Map[String, mutable.Map[String, mutable.Seq[Car]]] = {

    val schedule = new Schedule().readSchedule()
    val data = mutable.Map[String, mutable.Map[String, mutable.Seq[Car]]]()

    for ((carId: Int, record: Map[String, CarItinerary]) <- schedule) {
      for ((time: String, itinerary: CarItinerary) <- record) {
        val stName = itinerary.departure
        if (!data.contains(time))
          data.put(time, mutable.Map[String, mutable.Seq[Car]]())
        if (!data(time).contains(stName))
          data(time).put(stName, mutable.Seq[Car]())
        data(time)(stName) :+= new Car(carId, itinerary.maxCapacity, stName, this)
      }
    }

    data

  }

  private def loadPassengers(): mutable.Map[String, mutable.Map[String, mutable.Seq[Passenger]]] = {

    val dataReader = new DataReader()
    val data = mutable.Map[String, mutable.Map[String, mutable.Seq[Passenger]]]()

    for (station <- stations) {
      val stName = station.name
      for (passenger <- dataReader.getPassengers(stName)) {
        val time = passenger.time
        if (!data.contains(time))
          data.put(time, mutable.Map[String, mutable.Seq[Passenger]]())
        if (!data(time).contains(stName))
          data(time).put(stName, mutable.Seq[Passenger]())
        data(time)(stName) :+= passenger
      }
    }

    data

  }

}
