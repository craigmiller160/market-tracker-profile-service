package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import kotlinx.coroutines.Deferred

typealias ChildDownloadServiceResult = Deferred<TryEither<List<PortfolioWithHistory>>>

interface ChildDownloaderService {
  suspend fun download(token: String): ChildDownloadServiceResult
}
