package cn.zenliu.spring.security.component.reactive

import cn.zenliu.spring.security.properties.AuthProperties
import cn.zenliu.spring.security.repository.AuthAuthenticationRepository
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.toMono

class AuthServerAuthenticationConverter(
    authenticationManager: ReactiveAuthenticationManager,
    private val prop: AuthProperties,
    private val repo: AuthAuthenticationRepository
) : AuthenticationWebFilter(authenticationManager) {
    init {
        setServerAuthenticationConverter { exchange ->
            val token = exchange.request.headers[prop.tokenName]?.firstOrNull()
            val validation = repo.isValidToken(token)
            when {
                token == null && prop.exceptionIfTokenMissing -> throw AuthenticationCredentialsNotFoundException("Preauthentication ${prop.tokenName} not found in header")
                !validation && prop.enableAnonymous -> AnonymousAuthenticationToken(
                    token?: exchange.request.id,
                    "${token?: exchange.request.remoteAddress?: exchange.request.uri}",
                    mutableSetOf(SimpleGrantedAuthority(prop.anonymousAuthority))
                )
                else -> repo.loadFromToken(token!!)
            }.let {
                it ?: throw ResponseStatusException(
                    HttpStatus.valueOf(prop.tokenFailedStatusCode),
                    prop.tokenFailedMessage
                )
            }.toMono()
        }
    }

}
