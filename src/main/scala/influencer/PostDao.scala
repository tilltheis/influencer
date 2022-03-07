package influencer

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

trait PostDao {
  def loadPosts: IO[Vector[Post]]
  def createPost(post: Post): IO[Unit]
}

class PostDaoImpl(xa: Transactor[IO]) extends PostDao {
  def loadPosts: IO[Vector[Post]] =
    sql"SELECT * FROM post ORDER BY created_at DESC".query[Post].to[Vector].transact(xa)

  def createPost(post: Post): IO[Unit] =
    sql"INSERT INTO post (id, created_at, uri) VALUES (${post.id}, ${post.createdAt}, ${post.uri})".update.run
      .transact(xa)
      .void
}
