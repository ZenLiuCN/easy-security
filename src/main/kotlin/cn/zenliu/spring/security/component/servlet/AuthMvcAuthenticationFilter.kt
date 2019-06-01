package cn.zenliu.spring.security.component.servlet

import cn.zenliu.spring.security.properties.AuthProperties
import cn.zenliu.spring.security.repository.AuthAuthenticationRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ResponseStatusException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthMvcAuthenticationFilter(
    private val prop: AuthProperties,
    private val repo: AuthAuthenticationRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(prop.tokenName)
        val validation = repo.isValidToken(token)
        when {
            token == null && prop.exceptionIfTokenMissing -> throw AuthenticationCredentialsNotFoundException("Preauthentication ${prop.tokenName} not found in header")
            !validation && prop.enableAnonymous -> AnonymousAuthenticationToken(
                "${token ?: System.currentTimeMillis().toString()}",
                "${token
                    ?: request.remoteAddr
                    ?: request.requestURI}", mutableSetOf(SimpleGrantedAuthority(prop.anonymousAuthority))
            )
            else -> repo.loadFromToken(token)
        }.let {
            if (it == null) {
                throw ResponseStatusException(HttpStatus.valueOf(prop.tokenFailedStatusCode), prop.tokenFailedMessage)
            } else {
                it
            }
        }.let {
            SecurityContextHolder.getContext().authentication = it
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

}
