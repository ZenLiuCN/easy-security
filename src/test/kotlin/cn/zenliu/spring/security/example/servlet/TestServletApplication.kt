package cn.zenliu.spring.security.example.servlet

import cn.zenliu.spring.security.properties.AuthProperties
import org.eclipse.jetty.server.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.jetty.*
import org.springframework.context.annotation.*

fun main(args: Array<String>) {
    runApplication<TestServletApplication>(*args)
}

@SpringBootApplication
class TestServletApplication{
	@Bean
	fun jettyServerCustomizer(@Value("\${http.port:8080}") httpPort: String) = JettyServerCustomizer {
		it.addConnector(ServerConnector(it).apply {
			this.port = httpPort.toInt()
		})
	}
	@Bean
	fun jettyServletWebServerFactory(customizer: JettyServerCustomizer): JettyServletWebServerFactory {
		return JettyServletWebServerFactory().apply {
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
