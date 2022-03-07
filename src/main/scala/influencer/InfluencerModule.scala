package influencer

import cats.effect.{IO, Resource}

final case class InfluencerModule(postDao: PostDao)

object InfluencerModule extends (PostDao => InfluencerModule) {
  def resource(databaseModule: DatabaseModule): Resource[IO, InfluencerModule] = {
    Resource.pure(InfluencerModule(new PostDaoImpl(databaseModule.xa)))
  }
}
