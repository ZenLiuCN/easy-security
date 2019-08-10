package cn.zenliu.spring.security.example.reactive

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.toMono

@RestController
class Controller(private val auth: AuthConfiguration) {
    @PreAuthorize("permitAll() or isAnonymous()")
    @GetMapping("/login")
    fun login() = auth.doLogin().toMono()

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/status")
    fun status() = auth.status().toMono()

	@GetMapping("/https")
	fun https() = "hello https".toMono()

	@GetMapping("/http")
	fun http() = "hello http".toMono()
}
