# easy wapper for spring security
make it easy to use spring security with srpingmvc or webflux

## useage
implementation of AuthAuthneticationRepository
```kotlin
interface AuthAuthenticationRepository {
	/**
	 * function to validate token
	 * @param token String
	 * @return Boolean
	 */
	fun validateToken(token: String?): Boolean

	/**
	 * load user Authentication by token,failed with null
	 * @param token String
	 * @return PreAuthenticatedAuthenticationToken?
	 */
	fun loadFromToken(token: String): PreAuthenticatedAuthenticationToken?
}
```
Implementation of PreAuthenticatedAauthenticationToken
```kotlin
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
```
controll access
```ktolin
@RestController
class Controller(private val auth: AuthConfiguration) {
    @PreAuthorize("permitAll() or isAnonymous()") //Any one can access
    @GetMapping("/login")
    fun login() = auth.doLogin().toMono()

    @PreAuthorize("hasAnyRole('ROLE_USER')") //only user with ROLE_USER can access
    @GetMapping("/status")
    fun status() = auth.status().toMono()

}
```
## more detialed example plz to see test in source
**note** should comment `starter-web` dependency before test on reactive environment
