package icu.crepusculumx.crepusserver.websockets.messages

import kotlinx.serialization.Serializable

@Serializable
data class BlogInfoReq(val path: String)