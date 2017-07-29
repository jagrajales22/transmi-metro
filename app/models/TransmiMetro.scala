package models

class TransmiMetro {

  val schedule: Schedule = new Schedule()

  val stations: Seq[Station] =
    Seq[Station](
      new Station("Portal americas", true, this),
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

  def start(): Unit = {
    // init schedule
  }

  def getCarStop(car: Car, time: String): CarStop = {
    schedule.getCarStop(car, time)
  }

}
