package cn.zenliu.spring.security.repository

import org.springframework.security.web.authentication.preauth.*
import org.springframework.stereotype.*

interface AuthAuthenticationRepository {
	/**
	 * 验证Token是否有效格式
	 * @param token String
	 * @return Boolean
	 */
	fun isValidToken(token: String?): Boolean

	/**
	 * 加载Token的缓存用户信息,失败返回null
	 * @param token String
	 * @return PreAuthenticatedAuthenticationToken?
	 */
	fun loadFromToken(token: String): PreAuthenticatedAuthenticationToken?
}
