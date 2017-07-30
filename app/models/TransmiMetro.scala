package models

import scala.collection.mutable

object TransmiMetro {
  val DATA_PATH: String = "./data/"
}
class TransmiMetro {

  val schedule: Schedule = new Schedule().readSchedule()

  // { time => { station => [passengers] } }
  val passengers: mutable.Map[String, mutable.Map[String, mutable.Seq[Passenger]]] =
    loadPassengers()

  // { time => { station => { arrivals: n, departures: m } } }
  val log: mutable.Map[String, mutable.Map[String, mutable.Map[String, Int]]] =
    mutable.Map[String, mutable.Map[String, mutable.Map[String, Int]]]()

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

  def getCarStop(car: Car, time: String): CarStop = {
    schedule.getCarStop(car, time)
  }

  def log(time: String, station: String, stat: String, num: Int): Unit = {
    if (!log.contains(time))
      log.put(time, mutable.Map[String, mutable.Map[String, Int]]())
    if (!log(time).contains(station))
      log(time).put(station, mutable.Map("arrivals" -> 0, "departures" -> 0))
    val current = log(time)(station)(stat)
    log(time)(station).put(stat, current + num)
  }

  private def loadPassengers(): mutable.Map[String, mutable.Map[String, mutable.Seq[Passenger]]] = {
    // ToDo
    null
  }

}
