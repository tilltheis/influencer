package influencer

import cats.syntax.traverse._
import org.http4s.implicits.http4sLiteralsSyntax

import java.time.{Instant, OffsetDateTime, ZoneId}

class PostDaoImplIntegrationSpec extends InfluencerDbSpec {
  "loadPosts" should {
    "load nothing when DB is empty" in {
      val dao = new PostDaoImpl(xa)
      dao.loadPosts.ioValue should ===(Vector.empty)
    }

    "load posts, newest to oldest" in {
      val dao = new PostDaoImpl(xa)

      val posts = Vector(
        Post(PostId("postId1"), OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneId.of("UTC")), uri"/1"),
        Post(PostId("postId2"), OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneId.of("UTC")), uri"/2"),
        Post(PostId("postId3"), OffsetDateTime.ofInstant(Instant.ofEpochMilli(3), ZoneId.of("UTC")), uri"/3")
      )

      posts.traverse(dao.createPost).ioValue

      dao.loadPosts.ioValue should ===(posts.reverse)
    }
  }

  "createPost" should {
    "already be tested by 'loadPosts'" in succeed
  }
}
