package cn.zenliu.spring.security.properties

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.*

@Component
@PropertySources(
	value = [
		(PropertySource("classpath:/authnetication.properties", ignoreResourceNotFound = true))
	]
)
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
	//define with https Map
	var http: Int=8080
	var https:Int=8081
}
