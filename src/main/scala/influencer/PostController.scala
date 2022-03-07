package influencer

import cats.effect.IO
import org.http4s.dsl.io._
import org.http4s.headers.Location
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.twirl._
import org.http4s.{Headers, HttpRoutes, Response, Status}

import java.time.OffsetDateTime
import java.util.UUID

class PostController(postDao: PostDao) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        posts <- postDao.loadPosts
        html = influencer.html.index(posts)
        response <- Ok(html)
      } yield response

    case request @ POST -> Root =>
      for {
        dto <- request.as[PostDto]

        // the following should be done in a service w/ injected uuid gen and clock
        id        <- IO(PostId(UUID.randomUUID().toString))
        createdAt <- IO(OffsetDateTime.now())
        _         <- postDao.createPost(dto.toPost(id, createdAt))
      } yield Response(Status.SeeOther, headers = Headers(Location(uri"/")))
  }
}
