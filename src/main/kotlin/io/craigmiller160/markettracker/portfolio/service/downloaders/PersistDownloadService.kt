package io.craigmiller160.markettracker.portfolio.service.downloaders

import com.github.michaelbull.result.combine
import com.github.michaelbull.result.map
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersistDownloadService(private val portfolioRepository: PortfolioRepository) {
  @Transactional
  suspend fun persistPortfolios(
      portfolios: List<PortfolioWithHistory>
  ): KtResult<List<PortfolioWithHistory>> = portfolios.map { createPortfolio(it) }.combine()

  private suspend fun createPortfolio(
      portfolio: PortfolioWithHistory
  ): KtResult<PortfolioWithHistory> {
    return portfolioRepository.createPortfolio(portfolio).map { portfolio }
  }
}
