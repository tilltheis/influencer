import cats.data.EitherT
import cats.effect.{IO, Resource}
import doobie.Meta
import org.http4s.{DecodeFailure, EntityDecoder, MalformedMessageBodyFailure, Uri, UrlForm}

import java.time.OffsetDateTime

package object influencer {
  final case class PostDto(uri: Uri) {
    def toPost(id: PostId, createdAt: OffsetDateTime): Post = Post(id, createdAt, uri)
  }

  final case class PostId(value: String) extends AnyVal
  final case class Post(id: PostId, createdAt: OffsetDateTime, uri: Uri)

  implicit val postDecoder: EntityDecoder[IO, PostDto] =
    EntityDecoder[IO, UrlForm].flatMapR { urlForm =>
      for {
        uriString <- EitherT
          .fromOption[IO](urlForm.getFirst("url"), MalformedMessageBodyFailure("missing uri"): DecodeFailure)
        uri <- EitherT
          .fromEither[IO](Uri.fromString(uriString))
          .leftMap(x => MalformedMessageBodyFailure("uri not in uri format", x.cause): DecodeFailure)
      } yield PostDto(uri)
    }

  implicit val postIdMeta: Meta[PostId] = Meta[String].timap(PostId)(_.value)
  implicit val uriMeta: Meta[Uri]       = Meta[String].timap(Uri.fromString(_).toOption.get)(_.toString)
}
