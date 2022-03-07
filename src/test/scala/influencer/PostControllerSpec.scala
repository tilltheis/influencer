package influencer

import cats.effect.IO
import org.http4s.headers.{Location, `Content-Type`}
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Charset, MediaType, Method, Request, Status, UrlForm}
import org.mockito.{ArgumentMatchersSugar, IdiomaticMockito}
import org.scalatest.OptionValues

import java.time.{Instant, OffsetDateTime, ZoneId}

class PostControllerSpec extends InfluencerSpec with OptionValues with IdiomaticMockito with ArgumentMatchersSugar {

  "GET /" should {
    "show index html" in {
      val dao = mock[PostDao]
      dao.loadPosts shouldReturn IO.pure(
        Vector(
          Post(
            PostId("postId"),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("UTC")),
            uri"https://example.org/example.jpg"
          )
        )
      )

      val controller = new PostController(dao)

      val request  = Request[IO](Method.GET, uri"/")
      val response = controller.routes.run(request).value.ioValue.value

      response.status should ===(Status.Ok)
      response.headers.get[`Content-Type`].value should ===(`Content-Type`(MediaType.text.html, Charset.`UTF-8`))

      response.bodyText.compile.string.ioValue should include("https://example.org/example.jpg")
    }
  }

  "POST /" should {
    "store post and redirect to index page" in {
      val dao = mock[PostDao]
      dao.createPost(any) shouldReturn IO.unit

      val controller = new PostController(dao)

      val form     = UrlForm("url" -> "https://example.org/example.jpg")
      val request  = Request[IO](Method.POST, uri"/").withEntity(form)
      val response = controller.routes.run(request).value.ioValue.value

      response.status should ===(Status.SeeOther)
      response.headers.get[Location].value should ===(Location(uri"/"))

      // uuid gen and clock should be injected
      val expectedUri = uri"https://example.org/example.jpg"
      dao.createPost(argMatching { case Post(_, _, `expectedUri`) => }) wasCalled once
    }
  }
}
