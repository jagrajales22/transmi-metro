package models

class Car(val id: Int, val capacity: Int, var current: String, tm: TransmiMetro) {

  var passengers: List[Passenger] = List[Passenger]()

  def update(time: String): Unit = {
    current = tm.getCarStop(this, time).departure
  }

  // pre: the arrived list fits int car's capacity
  // pre: `current` was updated with the station the car is currently in
  def simulate(time: String, boardingPassengers: Iterable[Passenger]): Unit = {

    val departures = passengers.count(_.destination == current)
    tm.log(time, current, "departures", departures)

    passengers = passengers.filter(_.destination != current)
    passengers ++= boardingPassengers

  }

  // pre: `current` was updated with the station the car is currently in
  def availableSeats(): Int = {
    val departures = passengers.count(_.destination == current)
    capacity - departures
  }

}
