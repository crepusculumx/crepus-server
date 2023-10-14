package icu.crepusculumx.crepusserver.websockets.messages

import icu.crepusculumx.crepusserver.websockets.Sid
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@Serializable
data class RawMessage<T>(val route: String, val mid: String, val replyMid: String, val token: String, val data: T)

@Serializable
class Message(val sid: Sid, val rawMessage: String) {
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> getRawMessage(): RawMessage<T> {
        return Json.decodeFromString<RawMessage<T>>(rawMessage)
    }
}
