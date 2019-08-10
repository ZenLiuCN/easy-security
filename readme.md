# easy wapper for spring security
[![](https://jitpack.io/v/ZenLiuCN/easy-security.svg)](https://jitpack.io/#ZenLiuCN/easy-security)
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
## configuration
```kotlin
@ConfigurationProperties("authnetication")
class AuthProperties {
	var tokenName: String = "token"
	var tokenFailedStatusCode: Int = HttpStatus.UNAUTHORIZED.value()
	var tokenFailedMessage: String = ""
	var exceptionIfTokenMissing: Boolean = false
	var enableAnonymous: Boolean = true
	var anonymousAuthority: String = "ANONYMOUS"
	var rolePrefix: String = "ROLE_"
	var useCRSF: Boolean = false
	var permitUrl: Map<String, Collection<String>> = mapOf()
	/**
	 * those url will redirect to https
	 */
	var tlsOnly: Collection<String> = mutableListOf()
	//must defined while use tlsOnly
	var http: Int=8080
	var https:Int=8081
}

```
```yaml
spring:
  main:
    allow-bean-definition-overriding: true
authnetication:
  permitUrl:
    ROLE_ADMIN:
        - /hello
  tlsOnly:
    - /https
  http: 8011
  https: 8010
```
## more detialed example plz to see test in source
**note** should comment `starter-web` dependency before test on reactive environment
