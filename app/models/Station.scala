package models

import scala.collection.mutable

class Station(val name: String, hub: Boolean, tm: TransmiMetro) {

  // passengers in this station waiting to board cars, in FIFO order
  val passengers: mutable.Queue[Passenger] = mutable.Queue[Passenger]()

  def simulate(time: String, arrivedCars: List[Int], arrivedPassengers: List[Passenger]): Unit = {

    passengers.enqueue(arrivedPassengers: _*)

    for (carId <- arrivedCars) {
      val car = tm.getCar(carId)
      car.currentStation = name
      val available = car.availableSeats()
      val num = Math.min(passengers.size, available)
      val boardingPassengers = (1 to num).map(_ => passengers.dequeue())
      car.simulate(time, boardingPassengers)
    }

    tm.log(time, name, "arrivals", arrivedPassengers.size)

  }

}
