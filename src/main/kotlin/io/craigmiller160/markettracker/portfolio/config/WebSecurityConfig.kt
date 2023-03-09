package io.craigmiller160.markettracker.portfolio.config

import io.craigmiller160.markettracker.portfolio.security.JwtAuthConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val jwtAuthConverter: JwtAuthConverter) {
  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
      http
          .authorizeHttpRequests { it.requestMatchers("/**").fullyAuthenticated() }
          .oauth2ResourceServer { it.jwt().jwtAuthenticationConverter(jwtAuthConverter) }
          .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
          .build()
}
