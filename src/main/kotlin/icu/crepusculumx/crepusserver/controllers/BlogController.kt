package icu.crepusculumx.crepusserver.controllers

import icu.crepusculumx.crepusserver.data.BlogNamespaces
import icu.crepusculumx.crepusserver.data.BlogInfo
import icu.crepusculumx.crepusserver.data.BlogTreeData
import icu.crepusculumx.crepusserver.services.BlogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import kotlin.text.Charsets.UTF_8


@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/blog")
class BlogController {

    @Autowired
    private lateinit var blogService: BlogService

    @GetMapping("file-tree/{user}")
    fun getBlogTreeData(@PathVariable("user") user: String): BlogTreeData {
        return blogService.getBlogTreeDataByUserName(user)
    }

    @GetMapping("blog-info/{user}/{path}")
    fun getBlogInfo(@PathVariable("user") user: String, @PathVariable("path") path: String): BlogInfo {
        val blogPath = URLDecoder.decode(path, UTF_8)

        return blogService.getBlogInfo(blogPath)
    }

    @GetMapping("blog-namespaces")
    fun getBlogNamespaces(): BlogNamespaces {
        return blogService.getBlogNamespaces()
    }
}

