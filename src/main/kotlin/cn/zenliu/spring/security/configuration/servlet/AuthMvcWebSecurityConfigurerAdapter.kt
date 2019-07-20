package cn.zenliu.spring.security.configuration.servlet

import cn.zenliu.spring.security.component.servlet.AuthMvcAuthenticationFilter
import cn.zenliu.spring.security.component.servlet.AuthMvcAuthenticationProvider
import cn.zenliu.spring.security.properties.AuthProperties
import cn.zenliu.spring.security.repository.AuthAuthenticationRepository

import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*
import org.springframework.security.access.expression.*
import org.springframework.security.authentication.*
import org.springframework.security.config.annotation.authentication.builders.*
import org.springframework.security.config.annotation.method.configuration.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.core.*
import org.springframework.security.core.userdetails.*
import org.springframework.security.web.*
import org.springframework.security.web.access.expression.*
import org.springframework.security.web.authentication.preauth.*
import org.springframework.security.web.server.*


@Configuration
@ConditionalOnClass(SecurityWebFilterChain::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(AuthProperties::class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(
	prePostEnabled = true,
	securedEnabled = false,
	jsr250Enabled = false)
class AuthMvcWebSecurityConfigurerAdapter(
    private val prop: AuthProperties,
    private val repo: AuthAuthenticationRepository
) : WebSecurityConfigurerAdapter(true) {
	@Bean
	fun disableUserDetailsService(): UserDetailsService = UserDetailsService {
		object : UserDetails {
			override fun getAuthorities(): MutableCollection<out GrantedAuthority> = emptyArray<GrantedAuthority>().toMutableSet()
			override fun isEnabled(): Boolean = false
			override fun getUsername(): String = it
			override fun isCredentialsNonExpired(): Boolean = false
			override fun getPassword(): String = it
			override fun isAccountNonExpired(): Boolean = false
			override fun isAccountNonLocked(): Boolean = false

		}
	}

	override fun configure(http: HttpSecurity) {
        if (prop.useCRSF) {
            http.csrf().and()
        } else {
            http.csrf().disable()
        }

        prop.permitUrl.forEach { role,urls ->
            http.authorizeRequests()
                .antMatchers(*urls.toTypedArray())
                .access(role)
        }
		http.addFilterAt(
				AuthMvcAuthenticationFilter(prop, repo),
				RequestHeaderAuthenticationFilter::class.java

		)	/*.authorizeRequests()// 无需配置,交给控制层进行控制
			.anyRequest()
			.authenticated()
			.and()*/
			.anonymous()
			.and()
			.httpBasic().disable()
			.headers().disable()
			.logout().disable()
			.formLogin().disable()
			.sessionManagement().disable()


	}

	@Bean
	fun authenticationTrustResolver() = object : AuthenticationTrustResolver {
		override fun isRememberMe(authentication: Authentication?) = authentication is RememberMeAuthenticationToken
		override fun isAnonymous(authentication: Authentication?): Boolean = (authentication is AnonymousAuthenticationToken)

	}

	override fun configure(auth: AuthenticationManagerBuilder) {
		auth.authenticationProvider(AuthMvcAuthenticationProvider())
	}

	override fun configure(web: WebSecurity) {
		web.expressionHandler(object : DefaultWebSecurityExpressionHandler() {
			override fun createSecurityExpressionRoot(authentication: Authentication, fi: FilterInvocation): SecurityExpressionOperations {
				val root = super.createSecurityExpressionRoot(authentication, fi) as WebSecurityExpressionRoot
				root.setDefaultRolePrefix(prop.rolePrefix)
				return root
			}
		})
	}
}

