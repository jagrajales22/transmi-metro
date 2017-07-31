package models

import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import rx.lang.scala.Observable

import scala.collection.mutable
import scala.util.Try

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

  // { time => { station => { arrivals: n, departures: m, density: p } } }
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
      log(time).put(station, mutable.Map(
        "arrivals" -> 0, "departures" -> 0, "density" -> 0))
    val current = log(time)(station)(stat)
    log(time)(station).put(stat, current + num)
  }

  def simulate(): Unit = {

    var time = "0400"
    val endTime = "0000"

    while (time != endTime) {

      // simulate 1-minute updates with a 1-second sleep
      Thread.sleep(1000)

      for (station <- stations) {
        val stName = station.name
        // reactive car observer
        val carPos = getCarPositions(time, stName)
        // reactive passenger observer
        val boarding = getPassengers(time, stName)
        station.simulate(time, carPos, boarding)
      }

      time = nextTimeFromString(time)

    }

  }

  def reportDensity(station: String): Map[String, Int] = {
    generateReport(station, "density")
  }

  def reportArrivals(station: String): Map[String, Int] = {
    generateReport(station, "arrivals")
  }

  def reportDepartures(station: String): Map[String, Int] = {
    generateReport(station, "departures")
  }

  // private helpers

  private def getCarPositions(time: String, station: String): Observable[Int] = {
    try {
      requestCar(carPositions(time)(station).toList)
    } catch {
      case _: Exception => requestCar(List[Int]())
    }
  }

  private def requestCar(cars: List[Int]): Observable[Int] = {

    Observable(subscriber => {
      for (car <- cars) {
        if (!subscriber.isUnsubscribed) {
          subscriber.onNext(car)
        }
      }
      if (!subscriber.isUnsubscribed) {
        subscriber.onCompleted()
      }
    })

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

  private def getPassengers(time: String, station: String): Observable[Passenger] = {
    try {
      requestPassenger(passengers(time)(station).toList)
    } catch {
      case _: Exception => requestPassenger(List[Passenger]())
    }
  }

  private def requestPassenger(passengers: List[Passenger]): Observable[Passenger] = {

    Observable(subscriber => {
      for (passenger <- passengers) {
        if (!subscriber.isUnsubscribed) {
          subscriber.onNext(passenger)
        }
      }
      if (!subscriber.isUnsubscribed) {
        subscriber.onCompleted()
      }
    })

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

  private def generateReport(station: String, statName: String): Map[String, Int] = {

    var i = 0
    var data = 0
    var time = "0400"
    val endTime = "0000"
    var stat = mutable.Map[String, Int]()

    while (time != endTime) {

      Try(data += log(time)(station)(statName))

      // 10 minute intervals
      if (i % 10 == 0) {
        if (data > 0)
          stat += time -> data
        data = 0
      }

      i += 1
      time = nextTimeFromString(time)

    }

    stat

  }

}
