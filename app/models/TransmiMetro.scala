package models

class TransmiMetro {

  val stations: Seq[Station] =
    Seq[Station](
      new Station("Portal americas", true),
      new Station("Calle 42 sur"),
      new Station("Carrera 80"),
      new Station("Kennedy"),
      new Station("Avenida Boyaca"),
      new Station("Carrera 68"),
      new Station("Carrera 50"),
      new Station("NQS"),
      new Station("Narino"),
      new Station("Calle 1", true),
      new Station("Calle 10"),
      new Station("Calle 26"),
      new Station("Calle 45"),
      new Station("Calle 63"),
      new Station("Calle 72", true),
    )

}
