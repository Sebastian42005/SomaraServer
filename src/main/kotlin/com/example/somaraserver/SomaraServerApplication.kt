package com.example.somaraserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SomaraServerApplication

fun main(args: Array<String>) {
    runApplication<SomaraServerApplication>(*args)
}
