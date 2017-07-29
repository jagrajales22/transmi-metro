package models

import scala.collection.mutable.Queue

class Station(name: String, hub: Boolean = false) {

  var cars: List[Car] = List[Car]()
  var passengers: Queue[Passenger] = Queue[Passenger]()

}
