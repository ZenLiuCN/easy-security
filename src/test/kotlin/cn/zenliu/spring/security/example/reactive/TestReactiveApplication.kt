package cn.zenliu.spring.security.example.reactive


import cn.zenliu.spring.security.properties.AuthProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.servlet.config.annotation.EnableWebMvc

fun main(args: Array<String>) {
    runApplication<TestReactiveApplication>(*args)
}

@SpringBootApplication
@EnableWebFluxSecurity
@EnableWebFlux
@EnableConfigurationProperties(AuthProperties::class)
class TestReactiveApplication
