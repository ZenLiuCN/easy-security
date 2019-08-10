package cn.zenliu.spring.security.configuration.reactive

import cn.zenliu.spring.security.component.reactive.*
import cn.zenliu.spring.security.properties.*
import cn.zenliu.spring.security.repository.*
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*
import org.springframework.security.authentication.*
import org.springframework.security.config.annotation.method.configuration.*
import org.springframework.security.config.annotation.web.reactive.*
import org.springframework.security.config.web.server.*
import org.springframework.security.core.authority.mapping.*
import org.springframework.security.web.*
import org.springframework.security.web.authentication.preauth.*
import org.springframework.security.web.server.*
import org.springframework.security.web.server.util.matcher.*
import org.springframework.web.reactive.config.*
import reactor.core.publisher.*



@Configuration
@ConditionalOnClass(SecurityWebFilterChain::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(AuthProperties::class)
@EnableWebFlux
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity()
class ReactiveWebSecurityAutoConfigurer(
	private val prop: AuthProperties,
	private val repo: AuthAuthenticationRepository
) {
	@Bean
	fun reactiveAuthenticationManager() = ReactiveAuthenticationManager {
		when {
			it is PreAuthenticatedAuthenticationToken -> it.apply {
				isAuthenticated = true
			}
			it is AnonymousAuthenticationToken && prop.enableAnonymous -> it.apply {
				isAuthenticated = true
			}
			else -> it
		}.toMono()
	}

	@Bean
	fun authoritiesMapper(): GrantedAuthoritiesMapper {
		val mapper = SimpleAuthorityMapper()
		mapper.setPrefix(prop.rolePrefix) // this line is not required
		mapper.setConvertToUpperCase(true) // convert your roles to uppercase
		return mapper
	}

	@Bean
	fun securityWebFilterChain(
		http: ServerHttpSecurity,
		manager: ReactiveAuthenticationManager
	): SecurityWebFilterChain {
		if (prop.useCRSF) {
			http.csrf().and()
		} else {
			http.csrf().disable()
		}
		prop.permitUrl.forEach { role, urls ->
			http.authorizeExchange()
				.pathMatchers(*urls.toTypedArray())
				.apply {
					when {
						role.contains("isAnonymous") -> this.permitAll()
						else -> hasAuthority(role)
					}
				}

		}

		prop.tlsOnly.takeIf { it.isNotEmpty() }?.let {
			val mapper = PortMapperImpl().apply {
				this.translatedPortMappings.put(prop.http, prop.https)
			}

			it.forEach {
				http
					.securityMatcher(ServerWebExchangeMatchers.pathMatchers(it))
					.redirectToHttps()
					.portMapper(mapper)
			}


		}
		return http
			.addFilterAt(
				AuthServerAuthenticationConverter(
					manager,
					prop,
					repo
				), SecurityWebFiltersOrder.FIRST
			).httpBasic().disable()
			.logout().disable()
			.formLogin().disable()
			.authenticationManager(manager)
			.authorizeExchange().anyExchange().authenticated()
			.and()
			.build()
	}
}

