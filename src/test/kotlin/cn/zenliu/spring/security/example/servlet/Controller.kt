package cn.zenliu.spring.security.example.servlet

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.toMono

@RestController
class Controller(private val auth: AuthConfiguration) {
    @PreAuthorize("permitAll() or isAnonymous()")
    @GetMapping("/login")
    fun login() = auth.doLogin()

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/status")
    fun status() = auth.status()

    @GetMapping("/hello")
    fun hello() = "hello"

	@GetMapping("/https")
	fun https() = "hello https"

	@GetMapping("/http")
	fun http() = "hello http"

}
