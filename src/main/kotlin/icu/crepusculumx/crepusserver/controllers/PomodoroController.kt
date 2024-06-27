package icu.crepusculumx.crepusserver.controllers

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/pomodoro")
class PomodoroController {
    private val pomodoroFileRootPath = System.getProperty("user.dir") + "/resources/public/pomodoro/"

    fun getTodayFilePath(): String {
        return pomodoroFileRootPath + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json"
    }


    private inline fun <reified T> readJsonFile(filePath: String): T {
        val file = File(filePath)
        RandomAccessFile(filePath, "r").use { raf ->
            val channel = raf.channel
            channel.lock(0, Long.MAX_VALUE, true).use { _ ->
                val buffer = ByteArray(file.length().toInt())
                raf.read(buffer)
                return Json.decodeFromString<T>(String(buffer, StandardCharsets.UTF_8))
            }
        }
    }

    private inline fun <reified T> writeJsonFile(filePath: String, data: T) {
        val file = File(filePath)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        RandomAccessFile(filePath, "rw").use { raf ->
            val channel = raf.channel
            channel.lock(0, Long.MAX_VALUE, true).use { _ ->
                raf.setLength(0)
                raf.write(Json.encodeToString(data).toByteArray(StandardCharsets.UTF_8))
            }
        }
    }

    @GetMapping("/types")
    fun getTypes(): ArrayList<String> {
        val filePath = "${pomodoroFileRootPath}/types.json"
        val file = File(filePath)
        if (!file.exists()) {
            writeJsonFile(filePath, ArrayList<String>())
        }

        return readJsonFile<ArrayList<String>>(filePath)
    }

    @GetMapping("/today")
    fun getPomodoroInfos(): PomodoroInfos {
        val filePath = getTodayFilePath()
        val file = File(filePath)
        if (!file.exists()) {
            return ArrayList()
        }

        return readJsonFile<PomodoroInfos>(filePath)
    }

    @PostMapping("pomodoro-info")
    fun addPomodoroInfo(@RequestBody pomodoroInfo: PomodoroInfo) : PomodoroInfo {
        val todayPomodoroInfos = getPomodoroInfos()
        todayPomodoroInfos.add(pomodoroInfo)

        val filePath = getTodayFilePath()
        writeJsonFile(filePath, todayPomodoroInfos)
        return pomodoroInfo
    }
}


@Serializable
data class PomodoroInfo(val startTime: Long, val endTime: Long, val message: String, val type: String)

typealias PomodoroInfos = ArrayList<PomodoroInfo>