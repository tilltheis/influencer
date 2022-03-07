package influencer

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import influencer.InfluencerDbSpec.{TruncateBeforeAll, TruncateBeforeEach, TruncateBehavior}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

object InfluencerDbSpec {
  sealed trait TruncateBehavior
  object TruncateBeforeEach extends TruncateBehavior
  object TruncateBeforeAll  extends TruncateBehavior
}

class InfluencerDbSpec extends InfluencerSpec with BeforeAndAfterAll with BeforeAndAfterEach {
  var xa: Transactor[IO]              = _
  private var closeDatabase: IO[Unit] = _

  protected val truncateBehavior: TruncateBehavior = TruncateBeforeEach

  override def beforeAll(): Unit = {
    super.beforeAll()

    val (databaseModule, closeDatabase) =
      DatabaseModule.resource("influencer-test", "influencer-test", "influencer-test").allocated.ioValue
    this.xa = databaseModule.xa
    this.closeDatabase = closeDatabase

    if (truncateBehavior == TruncateBeforeAll) truncateDb()
  }

  override def afterAll(): Unit = {
    closeDatabase.ioValue
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()

    if (truncateBehavior == TruncateBeforeEach) truncateDb()
  }

  private def truncateDb(): Unit = {
    sql"TRUNCATE TABLE post".update.run.transact(xa).void.ioValue
  }
}
