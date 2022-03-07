package influencer

import cats.effect.IO
import influencer.InfluencerDbSpec.{TruncateBeforeAll, TruncateBehavior}
import org.http4s.Method.POST
import org.http4s.Status.SeeOther
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Request, UrlForm}

class AppIntegrationSpec extends InfluencerDbSpec {
  private var stopApp: IO[Unit] = _

  override protected val truncateBehavior: TruncateBehavior = TruncateBeforeAll

  override def beforeAll(): Unit = {
    super.beforeAll()

    val stopApp = App.resource(List("--test")).allocated.ioValue._2
    this.stopApp = stopApp
  }

  override def afterAll(): Unit = {
    stopApp.ioValue
    super.afterAll()
  }

  private val indexUri = uri"http://localhost:8081/"

  "first visit" should {
    "show no posts" in {
      BlazeClientBuilder[IO].resource.use { client =>
        IO {
          val html = client.expect[String](indexUri).ioValue
          html should include("Nothing here.")
        }
      }.ioValue
    }
  }

  "posting influences" should {
    "result in redirects" in {
      BlazeClientBuilder[IO].resource.use { client =>
        IO {
          client.status(Request[IO](POST, indexUri).withEntity(UrlForm("url" -> "/foo"))).ioValue should ===(SeeOther)
          client.status(Request[IO](POST, indexUri).withEntity(UrlForm("url" -> "/bar"))).ioValue should ===(SeeOther)
        }
      }.ioValue
    }
  }

  "visiting the index page again" should {
    "show the influences that have just been posted" in {
      BlazeClientBuilder[IO].resource.use { client =>
        IO {
          val html = client.expect[String](indexUri).ioValue
          html should include("/foo")
          html should include("/bar")
        }
      }.ioValue
    }
  }
}
