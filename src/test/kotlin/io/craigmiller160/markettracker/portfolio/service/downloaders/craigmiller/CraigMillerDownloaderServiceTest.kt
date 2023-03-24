package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.getOrThrow
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import java.lang.AssertionError
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@MarketTrackerPortfolioIntegrationTest
class CraigMillerDownloaderServiceTest
@Autowired
constructor(
    @Value("\${downloaders.craigmiller.test-port}") private val testPort: Int,
    private val service: CraigMillerDownloaderService,
    private val objectMapper: ObjectMapper,
    private val downloaderConfig: CraigMillerDownloaderConfig,
) {
  companion object {
    private val transactions1: String = DataLoader.load("data/craigmiller/Transactions1.json")
  }

  private val mockServer = MockWebServer()

  @BeforeEach
  fun setup() {
    mockServer.start(testPort)
  }

  @AfterEach
  fun cleanup() {
    mockServer.shutdown()
  }

  @Test
  fun `downloads and formats google sheet data`() {
    val googleApiAccessToken =
        GoogleApiAccessToken(accessToken = "TOKEN", expiresIn = 100000, tokenType = "Bearer")
    mockServer.enqueue(
        MockResponse().apply {
          status = "HTTP/1.1 200 OK"
          setBody(objectMapper.writeValueAsString(googleApiAccessToken))
          addHeader("Content-Type", "application/json")
        })

    repeat(3) {
      mockServer.enqueue(
          MockResponse().apply {
            status = "HTTP/1.1 200 OK"
            setBody(transactions1)
            addHeader("Content-Type", "application/json")
          })
    }

    val result = runBlocking { service.download() }.getOrThrow()

    result.shouldHaveSize(3)

    result.forEach { portfolio ->
      portfolio.name.shouldBeIn("Brokerage", "Roth IRA", "Rollover IRA")
      val expectedSharesOwned =
          TEST_DATA.map { it.copy(portfolioId = portfolio.id, userId = downloaderConfig.userId) }
              .sortedWith(::sort)
      val actualSharesOwned = portfolio.ownershipHistory.sortedWith(::sort)

      actualSharesOwned.shouldHaveSize(expectedSharesOwned.size).forEachIndexed { index, actual ->
        ktRunCatching {
              val expected = expectedSharesOwned[index]
              actual.userId.shouldBeEqualComparingTo(expected.userId)
              actual.portfolioId.shouldBeEqualComparingTo(expected.portfolioId)
              actual.dateRangeStart.shouldBeEqualComparingTo(expected.dateRangeStart)
              actual.dateRangeEnd.shouldBeEqualComparingTo(expected.dateRangeEnd)
              actual.symbol.shouldBeEqualComparingTo(expected.symbol)
              actual.totalShares.shouldBeEqualComparingTo(expected.totalShares)
            }
            .getOrThrow { ex -> AssertionError("Error validating record $index", ex) }
      }
    }
  }
}

private fun sort(one: SharesOwned, two: SharesOwned): Int {
  val symbolCompare = one.symbol.compareTo(two.symbol)
  if (symbolCompare != 0) {
    return symbolCompare
  }
  return one.dateRangeStart.compareTo(two.dateRangeStart)
}
