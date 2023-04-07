package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class SharesOwnedRouteParams(
    val stockSymbol: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val interval: SharesOwnedInterval,
    val userId: TypedId<UserId>,
    val portfolioId: TypedId<PortfolioId>? = null
)

fun createSharesOwnedRouteData(
    data: PortfolioRouteData,
    params: SharesOwnedRouteParams
): List<SharesOwnedResponse> {
  val sharesOwned =
      data.sharesOwned
          .asSequence()
          .filter { record -> record.userId == params.userId }
          .filter { record ->
            params.portfolioId == null || params.portfolioId == record.portfolioId
          }
          .filter { record -> record.symbol == params.stockSymbol }
          .filter { record ->
            (record.dateRangeStart >= params.startDate &&
                record.dateRangeStart <= params.endDate) || record.dateRangeEnd >= params.startDate
          }
          .sortedBy { it.dateRangeStart }
          .toList()

  return createResponseDates(params).map { date ->
    SharesOwnedResponse(date = date, shares = BigDecimal("0"))
  }
}

private fun createResponseDates(params: SharesOwnedRouteParams): List<LocalDateTime> {
  val (intervalCount, modifier) = getValuesForInterval(params)
  val base = params.startDate.atStartOfDay()

  return (0 until intervalCount).map { index -> modifier(base, index) }
}

private fun getValuesForInterval(params: SharesOwnedRouteParams): IntervalValues =
    when (params.interval) {
      SharesOwnedInterval.MINUTELY ->
          IntervalValues(
              intervalCount =
                  ChronoUnit.MINUTES.between(
                      params.startDate.atStartOfDay(), params.endDate.atStartOfDay()),
              modifier = { base, index -> base.plusMinutes(index) })
      SharesOwnedInterval.DAILY ->
          IntervalValues(
              intervalCount = ChronoUnit.DAYS.between(params.startDate, params.endDate),
              modifier = { base, index -> base.plusDays(index) })
      SharesOwnedInterval.WEEKLY ->
          IntervalValues(
              intervalCount = ChronoUnit.WEEKS.between(params.startDate, params.endDate),
              modifier = { base, index -> base.plusWeeks(index) })
      SharesOwnedInterval.MONTHLY ->
          IntervalValues(
              intervalCount = ChronoUnit.MONTHS.between(params.startDate, params.endDate),
              modifier = { base, index -> base.plusMonths(index) })
    }

private typealias TimestampModifier = (LocalDateTime, Long) -> LocalDateTime

private data class IntervalValues(val intervalCount: Long, val modifier: TimestampModifier)
