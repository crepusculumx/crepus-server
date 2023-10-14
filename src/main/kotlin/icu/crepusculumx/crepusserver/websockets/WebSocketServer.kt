package icu.crepusculumx.crepusserver.websockets

import icu.crepusculumx.crepusserver.annotations.WebSocketRoute
import icu.crepusculumx.crepusserver.services.BlogService
import icu.crepusculumx.crepusserver.websockets.messages.Message
import icu.crepusculumx.crepusserver.websockets.messages.RawMessage
import jakarta.websocket.*
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.UUID


typealias Sid = String // WebSocketId

@Component
@ServerEndpoint("/{token}")
class WebSocketServer {

    var session: Session? = null
    var sid: Sid? = null

    @OptIn(DelicateCoroutinesApi::class)
    @OnMessage
    fun onMessage(message: String) {
        GlobalScope.launch {
            val t = Json.parseToJsonElement(message)
            val route =
                t.jsonObject.toMap()[RawMessage<Any>::route.name].toString().removePrefix("\"").removeSuffix("\"")

            if (route == "/reply") {
                val replyId = t.jsonObject.toMap()[RawMessage<Any>::replyMid.name].toString().removePrefix("\"")
                    .removeSuffix("\"")
                replyQueues[replyId]?.add(message)
            } else {
                val blogServiceClass: Class<*> = BlogService::class.java
                val methods = blogServiceClass.methods
                for (method in methods) {
                    val annotations = method.getAnnotationsByType(WebSocketRoute::class.java)
                    for (annotation in annotations) {
                        if (annotation.route == route) {
                            method.invoke(blogService, Message(sid!!, message))
                            return@launch
                        }
                    }
                }
            }
        }
    }

    @OnOpen
    fun onOpen(session: Session, @PathParam(value = "token") token: String) {
        this.session = session
        this.sid = token // use name as token temporarily
        webSocketServers[sid!!] = this
    }

    @OnClose
    fun onClose(reason: CloseReason) {
        webSocketServers.remove(this.sid)
        blogService!!.removeSocketById(this.sid!!)
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        error.printStackTrace()
    }

    @Synchronized
    fun sendMessage(message: String) {
        this.session?.basicRemote?.sendText(message)
    }

    companion object {
        @JvmField
        var SERVER_TOKEN: String = "crepus-server"

        @JvmField
        var blogService: BlogService? = null


        @JvmField
        val webSocketServers = ConcurrentHashMap<String, WebSocketServer>()

        @JvmField
        val replyQueues = ConcurrentHashMap<String, LinkedBlockingQueue<String>>()


        @OptIn(ExperimentalSerializationApi::class)
        inline fun <reified T> sendMessageById(
            sid: String,
            route: String,
            data: T,
            mid: String = UUID.randomUUID().toString()
        ) {
            val rawMessage = RawMessage(route, mid, "", SERVER_TOKEN, data)
            webSocketServers[sid]?.sendMessage(Json.encodeToString(rawMessage))
        }

        @OptIn(ExperimentalSerializationApi::class)
        inline fun <reified T> sendReplyMessageById(sid: String, mid: String, data: T) {
            val rawMessage = RawMessage("/reply", UUID.randomUUID().toString(), mid, SERVER_TOKEN, data)
            webSocketServers[sid]?.sendMessage(Json.encodeToString(rawMessage))
        }

        @OptIn(ExperimentalSerializationApi::class)
        inline fun <reified T, V> sendMessageByIdWithReply(sid: String, route: String, data: T): WebSocketReply<V> {
            val mid: String = UUID.randomUUID().toString()

            val webSocketReply = WebSocketReply<V>(replyQueues, sid, mid)
            sendMessageById(sid, route, data, mid)

            return webSocketReply
        }
    }
}

class WebSocketReply<T>(
    private val replyQueue: ConcurrentHashMap<String, LinkedBlockingQueue<String>>,
    private val sid: String,
    private val mid: String
) {
    init {
        replyQueue[mid] = LinkedBlockingQueue()
    }

    /**
     * Blocking
     */
    fun getReply(): Message {
        while (true) {
            val message = replyQueue[mid]?.take()
            if (message != null) {
                return Message(sid, message)
            }
        }
    }

    fun endReply() {
        replyQueue.remove(mid)
    }
}