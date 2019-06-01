package cn.zenliu.spring.security.example.servlet

import cn.zenliu.spring.security.properties.AuthProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<TestServletApplication>(*args)
}

@SpringBootApplication
@EnableConfigurationProperties(AuthProperties::class)
class TestServletApplication
