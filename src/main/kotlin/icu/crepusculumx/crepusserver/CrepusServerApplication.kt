package icu.crepusculumx.crepusserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrepusServerApplication

fun main(args: Array<String>) {
	runApplication<CrepusServerApplication>(*args)
}
