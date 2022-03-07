package influencer

import cats.effect.{IO, Resource}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.server.{Router, Server}
import org.http4s.{Response, Status}

final case class HttpModule(server: Server)

object HttpModule extends (Server => HttpModule) {
  def resource(influencerModule: InfluencerModule, port: Int): Resource[IO, HttpModule] = {
    val postController = new PostController(influencerModule.postDao)
    val plainApp       = Router("/" -> postController.routes).orNotFound
    val loggingApp     = Logger.httpApp(logHeaders = false, logBody = false)(plainApp)
    BlazeServerBuilder[IO]
      .bindHttp(port, "0.0.0.0")
      .withHttpApp(loggingApp)
      .withServiceErrorHandler { request =>
        { case error: Throwable =>
          IO {
            println(s"Encountered error when handling $request: $error")
            Response(Status.InternalServerError)
          }
        }
      }
      .resource
      .evalTap { server =>
        IO {
          println(s"Server listening on ${server.baseUri}.")
        }
      }
      .onFinalizeCase { exitCase =>
        IO {
          println(s"Server shutting down ($exitCase).")
        }
      }
      .map(HttpModule)
  }
}
