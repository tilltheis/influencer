package influencer

import cats.effect.{IO, Resource}
import doobie._
import doobie.implicits._

final case class DatabaseModule(xa: Transactor[IO])

object DatabaseModule extends (Transactor[IO] => DatabaseModule) {
  private def verifyDatabaseConnectionOrFail(xa: Transactor[IO]): IO[Unit] =
    sql"select true".query[Unit].unique.transact(xa)

  def resource(database: String, user: String, password: String): Resource[IO, DatabaseModule] = {
    val xa = Transactor
      .fromDriverManager[IO]("org.postgresql.Driver", s"jdbc:postgresql:$database", user, password)
    Resource.eval(verifyDatabaseConnectionOrFail(xa).as(DatabaseModule(xa)))
  }
}
