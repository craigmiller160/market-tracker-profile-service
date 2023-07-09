package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MarketTrackerPortfolioIntegrationTest
class DatabaseClientSharesOwnedRepositoryTest
@Autowired
constructor(
    private val client: CoroutineDatabaseClient,
    private val sharesOwnedRepo: DatabaseClientSharesOwnedRepository,
    private val portfolioRepo: DatabaseClientPortfolioRepository
) {
  companion object {
    private val USER_1_ID = TypedId<UserId>()
    private val USER_2_ID = TypedId<UserId>()
    private val PORTFOLIO_1_ID = TypedId<PortfolioId>()
    private val PORTFOLIO_2_ID = TypedId<PortfolioId>()
    private val PORTFOLIO_3_ID = TypedId<PortfolioId>()
  }

  // TODO add to tests to ensure that other user data isn't pulled out

  private fun executeScript(name: String, params: Map<String, Any>) =
      javaClass.classLoader
          .getResourceAsStream("sql/databaseClientSharesOwnedRepositoryTest/$name")!!
          .bufferedReader()
          .readText()
          .let { sql -> runBlocking { client.update(sql, params) } }

  @BeforeEach
  fun setup() {
    executeScript(
        "all_1.sql",
        mapOf(
            "portfolio1Id" to PORTFOLIO_1_ID,
            "portfolio2Id" to PORTFOLIO_2_ID,
            "portfolio3Id" to PORTFOLIO_3_ID,
            "user1Id" to USER_1_ID,
            "user2Id" to USER_2_ID))
  }

  @Test
  fun `gets the current shares owned for stock in portfolio`() {
    executeScript(
        "getsTheCurrentSharesOwnedForStockInPortfolio.sql",
        mapOf(
            "user1Id" to USER_1_ID,
            "user2Id" to USER_2_ID,
            "portfolio1Id" to PORTFOLIO_1_ID,
            "portfolio2Id" to PORTFOLIO_2_ID,
            "portfolio3Id" to PORTFOLIO_3_ID))
    TODO()
  }

  @Test
  fun `gets the current shares owned for stock in all portfolios combined`() {
    TODO()
  }

  @Test
  fun `gets the shares owned at an interval for stock in portfolio`() {
    TODO()
  }

  @Test
  fun `gets the shares owned at an interval for stock in all portfolios combined`() {
    TODO()
  }
}
