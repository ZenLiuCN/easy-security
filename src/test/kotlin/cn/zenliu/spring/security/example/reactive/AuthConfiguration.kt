package cn.zenliu.spring.security.example.reactive

import cn.zenliu.spring.security.properties.AuthProperties
import cn.zenliu.spring.security.repository.AuthAuthenticationRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class AuthConfiguration(private val prop: AuthProperties) : AuthAuthenticationRepository {
    private val pool = ConcurrentHashMap<String, AuthedToken>()
    override fun isValidToken(token: String?): Boolean = token?.let { pool.containsKey(it) } ?: false
    override fun loadFromToken(token: String): PreAuthenticatedAuthenticationToken? = pool.get(token)
    fun doLogin() = UUID.randomUUID().toString().replace("-", "").let { tk ->
        pool.put(
            tk, AuthedToken(
                tk, Instant.now().toString(), listOf(
                    Role(prop.rolePrefix + "USER"),
                    Role(prop.rolePrefix + "ADMIN")
                )
            )
        )
        tk
    }

    fun status() = pool
}

data class AuthedToken(
    val token: String,
    val user: String,
    val roles: List<Role>
) : PreAuthenticatedAuthenticationToken(token, user, roles)

data class Role(
    val role: String
) : GrantedAuthority {
    override fun getAuthority(): String = role
}
