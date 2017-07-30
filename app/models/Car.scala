package models

class Car(val id: Int, val capacity: Int, var currentStation: String, tm: TransmiMetro) {

  var passengers: List[Passenger] = List[Passenger]()

  def update(time: String): Unit = {
    currentStation = tm.getCarStop(this, time).departure
  }

  // pre: the arrived list fits int car's capacity
  // pre: `current` was updated with the station the car is currently in
  def simulate(time: String, boardingPassengers: Iterable[Passenger]): Unit = {
    val departures = passengers.count(_.destination == currentStation)
    tm.log(time, currentStation, "departures", departures)
    passengers = passengers.filter(_.destination != currentStation)
    passengers ++= boardingPassengers
  }

  // pre: `current` was updated with the station the car is currently in
  def availableSeats(): Int = {
    val departures = passengers.count(_.destination == currentStation)
    capacity - departures
  }

}
