package models

class Car(val id: Int, val capacity: Int, var current: Station, var destination: Station) {

  var passengers: List[Passenger] = List[Passenger]()

}
