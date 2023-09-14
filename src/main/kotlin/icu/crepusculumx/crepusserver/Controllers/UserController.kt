package icu.crepusculumx.crepusserver.Controllers

import org.springframework.web.bind.annotation.*
import java.io.File
import kotlin.collections.ArrayList


@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/user")
class UserController {
    private val blogFileRootPath = System.getProperty("user.dir") + "/resources/public/blog/"


    @GetMapping("/user-infos")
    fun getUserInfos(): UserInfos {
        val blogDir = File(blogFileRootPath)
        val files = blogDir.listFiles() ?: throw RuntimeException("getUserInfos() open blogFileRootPath failed");

        return UserInfos(
            files
                .filter { obj: File -> obj.isDirectory }
                .map { file: File ->
                    UserInfo(
                        file.name
                    )
                }
        )

    }

}


@JvmRecord
data class UserInfo(val userName: String)

typealias UserInfos = ArrayList<UserInfo>