package io.craigmiller160.markettracker.portfolio.testcore

import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.junit.jupiter.SpringExtension

class DatabaseCleaningExtension : BeforeEachCallback, AfterEachCallback {
  override fun beforeEach(context: ExtensionContext) {
    val databaseClient = getDatabaseClient(context)
    val sqlLoader = getSqlLoader(context)
    TODO("Not yet implemented")
  }

  private fun getDatabaseClient(context: ExtensionContext) =
      SpringExtension.getApplicationContext(context).getBean(DatabaseClient::class.java)
  private fun getSqlLoader(context: ExtensionContext) =
      SpringExtension.getApplicationContext(context).getBean(SqlLoader::class.java)

  override fun afterEach(context: ExtensionContext) {
    TODO("Not yet implemented")
  }
}
