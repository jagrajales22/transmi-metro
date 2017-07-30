package models

import org.joda.time.Period
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable

object TransmiMetro {
  val DATA_PATH: String = "./data/"
}

class TransmiMetro {

  // { car_id => car }
  val cars: mutable.Map[Int, Car] = mutable.Map[Int, Car]()

  // { time => { station, [car_ids] } }
  val carPositions: mutable.Map[String, mutable.Map[String, mutable.Buffer[Int]]] =
    loadCarPositions()

  // { time => { station => [passengers] } }
  val passengers: mutable.Map[String, mutable.Map[String, mutable.Buffer[Passenger]]] =
    loadPassengers()

  // { time => { station => { arrivals: n, departures: m } } }
  val log: mutable.Map[String, mutable.Map[String, mutable.Map[String, Int]]] =
    mutable.Map[String, mutable.Map[String, mutable.Map[String, Int]]]()

  // list of available stations
  val stations: List[Station] =
    List[Station](
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
      new Station("Calle 72", true, this))

  def getCar(carId: Int): Car = cars(carId)

  def log(time: String, station: String, stat: String, num: Int): Unit = {
    if (!log.contains(time))
      log.put(time, mutable.Map[String, mutable.Map[String, Int]]())
    if (!log(time).contains(station))
      log(time).put(station, mutable.Map("arrivals" -> 0, "departures" -> 0))
    val current = log(time)(station)(stat)
    log(time)(station).put(stat, current + num)
  }

  def simulate(): Unit = {

    var time = "0400"
    val endTime = "0000"

    while (time != endTime) {

      for (station <- stations) {
        val stName = station.name
        val carPos = getCarPositions(time, stName)
        val boarding = getPassengers(time, stName)
        station.simulate(time, carPos, boarding)
      }

      time = nextTimeFromString(time)

    }

  }

  // private helpers

  private def getCarPositions(time: String, station: String): List[Int] = {
    try {
      carPositions(time)(station).toList
    } catch {
      case _: Exception => List[Int]()
    }
  }

  private def getPassengers(time: String, station: String): List[Passenger] = {
    try {
      passengers(time)(station).toList
    } catch {
      case _: Exception => List[Passenger]()
    }
  }

  private def loadCarPositions(): mutable.Map[String, mutable.Map[String, mutable.Buffer[Int]]] = {

    val schedule = new Schedule().readSchedule()
    val data = mutable.Map[String, mutable.Map[String, mutable.Buffer[Int]]]()

    for ((carId: Int, record: Map[String, CarItinerary]) <- schedule) {
      for ((time: String, itinerary: CarItinerary) <- record) {
        val stName = itinerary.departure
        if (!data.contains(time))
          data.put(time, mutable.Map[String, mutable.Buffer[Int]]())
        if (!data(time).contains(stName))
          data(time).put(stName, mutable.Buffer[Int]())
        data(time)(stName).append(carId)
        // assume that the first occurrence of a car in the schedule
        // indicates its capacity and its starting station
        if (!cars.contains(carId))
          cars.put(carId, new Car(carId, itinerary.maxCapacity, stName, this))
      }
    }

    data

  }

  private def loadPassengers(): mutable.Map[String, mutable.Map[String, mutable.Buffer[Passenger]]] = {

    val dataReader = new DataReader()
    val data = mutable.Map[String, mutable.Map[String, mutable.Buffer[Passenger]]]()

    for (station <- stations) {
      val stName = station.name
      for (passenger <- dataReader.getPassengers(stName)) {
        val time = passenger.time
        if (!data.contains(time))
          data.put(time, mutable.Map[String, mutable.Buffer[Passenger]]())
        if (!data(time).contains(stName))
          data(time).put(stName, mutable.Buffer[Passenger]())
        data(time)(stName).append(passenger)
      }
    }

    data

  }

  private def nextTimeFromString(time: String): String = {
    val formatter = DateTimeFormat.forPattern("HHmm")
    val dat = formatter.parseDateTime(time)
    val next = dat.plus(Period.minutes(1))
    next.toString("HHmm")
  }

}
