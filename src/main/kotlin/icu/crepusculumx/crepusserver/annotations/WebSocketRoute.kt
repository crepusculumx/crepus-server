package icu.crepusculumx.crepusserver.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
annotation class WebSocketRoute(val route: String)
