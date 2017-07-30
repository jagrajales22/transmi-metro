package models

import scala.collection.mutable

class Station(val name: String, hub: Boolean, tm: TransmiMetro) {

  var passengers: mutable.Queue[Passenger] = mutable.Queue[Passenger]()

  def simulate(time: String, arrivedCars: List[Car], arrivedPassengers: List[Passenger]): Unit = {

    passengers ++= arrivedPassengers

    arrivedCars.foreach(car => {
      car.update(time)
      val available = car.availableSeats()
      val num = Math.min(passengers.size, available)
      val boardingPassengers = (1 to num).map(_ => passengers.dequeue())
      car.simulate(time, boardingPassengers)
    })

    tm.logCars(time, arrivedCars.size)

  }

}
