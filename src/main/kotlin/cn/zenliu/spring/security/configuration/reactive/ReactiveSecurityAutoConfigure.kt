package cn.zenliu.spring.security.configuration.reactive

import cn.zenliu.spring.security.component.reactive.AuthServerAuthenticationConverter
import cn.zenliu.spring.security.properties.AuthProperties
import cn.zenliu.spring.security.repository.AuthAuthenticationRepository

import org.springframework.boot.autoconfigure.condition.*
import org.springframework.context.annotation.*
import org.springframework.security.authentication.*
import org.springframework.security.config.annotation.method.configuration.*
import org.springframework.security.config.annotation.web.reactive.*
import org.springframework.security.config.web.server.*
import org.springframework.security.core.authority.mapping.*
import org.springframework.security.web.authentication.preauth.*
import org.springframework.security.web.server.*
import reactor.core.publisher.*
import reactor.netty.http.server.*


@Configuration
@ConditionalOnClass(HttpServer::class, SecurityWebFilterChain::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity()
class ReactiveSecurityAutoConfigure(
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

