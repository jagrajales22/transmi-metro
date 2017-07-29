package models

class Car(val id: Int, val capacity: Int, var current: String, tm: TransmiMetro) {

  var passengers: List[Passenger] = List[Passenger]()

  def update(time: String): Unit = {
    current = tm.getCarStop(this, time).departure
  }

  // pre: the arrived list fits int car's capacity
  // pre: `current` was updated with the station the car is currently in
  def simulate(time: String, boardingPassengers: Iterable[Passenger]): Unit = {
    passengers = passengers.filter(_.destination != current)
    passengers ++= boardingPassengers
    tm.logPassengers(time, passengers.size)
  }

  // pre: `current` was updated with the station the car is currently in
  def availableSeats(): Int = {
    val numLeaving = passengers.count(_.destination == current)
    capacity - numLeaving
  }

}
