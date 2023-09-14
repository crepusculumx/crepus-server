package icu.crepusculumx.crepusserver.Controllers

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import java.io.File
import java.net.URLDecoder
import kotlin.text.Charsets.UTF_8


@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/blog")
class BlogController {
    private val blogFileRootPath = System.getProperty("user.dir") + "/resources/public/blog/"

    private val themeSuffixes = HashSet(
        mutableListOf(
            "light", "dark"
        )
    )
    private val fileSuffixes = HashSet(
        mutableListOf(
            "md", "html",
            "jpg", "png",
            "pdf"
        )
    )


    private fun buildBlogTreeData(dir: File, prefix: String): BlogTreeData {
        val res = BlogTreeData()
        val blogs = HashSet<String>()
        val files = dir.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isHidden) {
                    continue
                }
                if (file.isFile) {
                    // blog-name.style-type.file-type -> blog-name
                    val fileNameParts = file.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val blogName = StringBuilder()
                    for (fileNamePart in fileNameParts) {
                        val suffixes: Set<String> = object : HashSet<String>() {
                            init {
                                addAll(fileSuffixes)
                                addAll(themeSuffixes)
                            }
                        }
                        if (suffixes.contains(fileNamePart)) {
                            break
                        }
                        blogName.append(fileNamePart)
                        blogName.append(".")
                    }
                    blogName.deleteCharAt(blogName.length - 1)
                    if (!blogs.contains(blogName.toString())) {
                        blogs.add(blogName.toString())

                        // /prefix/parent-path/
                        val blogPath = (file.parent + "/" + blogName).substring(prefix.length)
                        res.add(BlogTreeNode(blogPath, blogName.toString(), false, null))
                    }
                } else {
                    res.add(
                        BlogTreeNode(
                            file.path.substring(prefix.length),
                            file.name,
                            true,
                            buildBlogTreeData(file, prefix)
                        )
                    )
                }
            }
        }
        return res
    }

    @GetMapping("{fileName}")
    fun download(@PathVariable("fileName") fileName: String?, response: HttpServletResponse?) {
    }

    @GetMapping("file-tree/{user}")
    fun getBlogTreeData(@PathVariable("user") user: String): BlogTreeData {
        return buildBlogTreeData(File(blogFileRootPath + user), blogFileRootPath + user)
    }

    @GetMapping("blog-info/{user}/{path}")
    fun getBlogInfo(@PathVariable("user") user: String, @PathVariable("path") path: String?): BlogInfo {
        val blogPath = URLDecoder.decode(path, UTF_8)
        val lastSlashIndex = blogPath.lastIndexOf('/')
        val parentPath = blogPath.substring(0, lastSlashIndex)
        val blogName = blogPath.substring(lastSlashIndex + 1)
        val blogFileInfos: ArrayList<BlogFileInfo> = ArrayList<BlogFileInfo>()
        val parentDir = File(blogFileRootPath + user + parentPath)
        val files = parentDir.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.name.startsWith(blogName)) {
                    val fileNameParts = file.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val urlPath = parentPath + "/" + file.name
                    val fileType =
                        if (fileNameParts.size >= 2 && fileSuffixes.contains(fileNameParts[fileNameParts.size - 1])) fileNameParts[fileNameParts.size - 1] else "unknown"
                    val themeType =
                        if (fileNameParts.size >= 3 && themeSuffixes.contains(fileNameParts[fileNameParts.size - 2])) fileNameParts[fileNameParts.size - 2] else "default"
                    blogFileInfos.add(BlogFileInfo(urlPath, themeType, fileType))
                }
            }
        }
        return BlogInfo(blogPath, blogName, blogFileInfos)
    }
}


@JvmRecord
data class BlogFileInfo(val urlPath: String, val themeType: String, val fileType: String)


@JvmRecord
data class BlogInfo(val path: String, val title: String, val blogFileInfos: ArrayList<BlogFileInfo>)


@JvmRecord
data class BlogTreeNode(val path: String, val title: String, val isFolder: Boolean, val children: BlogTreeData?)


typealias BlogTreeData = ArrayList<BlogTreeNode>
