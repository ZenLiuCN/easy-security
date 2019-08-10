package cn.zenliu.spring.security.example.reactive


import cn.zenliu.spring.security.properties.AuthProperties
import org.eclipse.jetty.server.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.jetty.*
import org.springframework.context.annotation.*
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.config.EnableWebFlux

fun main(args: Array<String>) {
    runApplication<TestReactiveApplication>(*args)
}

@SpringBootApplication
class TestReactiveApplication{
	@Bean
	fun jettyServerCustomizer(@Value("\${http.port:8080}") httpPort: String) = JettyServerCustomizer {
		it.addConnector(ServerConnector(it).apply {
			this.port = httpPort.toInt()
		})
	}
	@Bean
	fun jettyReactiveWebServerFactory(customizer: JettyServerCustomizer): JettyReactiveWebServerFactory {
		return JettyReactiveWebServerFactory().apply {
			this.addServerCustomizers(/*JettyServerCustomizer { server ->
				(server.connectors.first() as ServerConnector).let { conn ->
					(conn.connectionFactories.first() as SslConnectionFactory).let {
						it.sslContextFactory.let { fact ->
							fact.endpointIdentificationAlgorithm = null
							fact.trustManagerFactoryAlgorithm = "PKIX"
							fact.keyManagerFactoryAlgorithm = "PKIX"
						}
					}
				}
			},*/customizer)
		}
	}
}
