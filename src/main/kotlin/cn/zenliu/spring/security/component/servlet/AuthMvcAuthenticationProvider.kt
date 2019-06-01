package cn.zenliu.spring.security.component.servlet

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken

class AuthMvcAuthenticationProvider : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        authentication.isAuthenticated = true
        return authentication
    }

    override fun supports(authentication: Class<*>): Boolean {
        return when {
            authentication == PreAuthenticatedAuthenticationToken::class.java ||
                    authentication == AnonymousAuthenticationToken::class.java
            -> true
            else -> false
        }
    }

}
