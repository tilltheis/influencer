package influencer

import cats.effect.{ExitCode, IO, IOApp, Resource}

object App extends IOApp {

  def resource(args: List[String]): Resource[IO, Unit] = {
    val (database, port) = if (args.headOption.contains("--test")) ("influencer-test", 8081) else ("influencer", 8080)
    for {
      databaseModule   <- DatabaseModule.resource(database, database, database)
      influencerModule <- InfluencerModule.resource(databaseModule)
      _                <- HttpModule.resource(influencerModule, port)
    } yield ()
  }

  override def run(args: List[String]): IO[ExitCode] = (for {
    _ <- resource(args)
    _ <- Resource.eval(IO {
      println("Press any key to stop the app.")
      val _ = io.StdIn.readLine()
    })
  } yield ()).use_.as(ExitCode.Success)

}
