package influencer

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt

trait InfluencerSpec extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  implicit class TestIO[A](io: IO[A]) {
    def ioValue: A = io.unsafeRunTimed(1.second).getOrElse(throw new TimeoutException)
  }

}
