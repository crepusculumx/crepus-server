package icu.crepusculumx.crepusserver

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class ResourceConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val path = System.getProperty("user.dir") + "/resources/public/"
        //配置静态资源访问路径
        registry.addResourceHandler("/public/**").addResourceLocations("file:$path")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        // 设置允许跨域的路径
        registry.addMapping("/**") // 设置允许跨域请求的域名
            .allowedOriginPatterns("*") // 是否允许证书
            .allowCredentials(false) // 设置允许的方法
            .allowedMethods("GET", "POST", "DELETE", "PUT") // 设置允许的header属性
            .allowedHeaders("*") // 跨域允许时间
            .maxAge((3600 * 24).toLong())
    }
}