package com.sdm.dashservice

import org.wasabi.app.AppServer
import java.io.File
import com.google.gson.Gson;
import com.github.salomonbrys.kotson.*
import khttp.post
import java.io.FileNotFoundException
import java.time.LocalDateTime

public class Configuration(
        val apiKey: String,
        val deviceId: String
                    )

private val CONFIG_FILE = "key.json"

fun loadConfig(fileName: String): Configuration? {
    val config: Configuration?

    try {
        config = Gson().fromJson<Configuration>(File(fileName).readText())
    } catch (e: FileNotFoundException) {
        println("config file not found. Please create a $CONFIG_FILE file with your api key and device ID")
        config = null
    }

    return config
}

public fun main(args: Array<String>) {
    var server = AppServer()
    var lastTaken: LocalDateTime? = null
    val config = loadConfig(CONFIG_FILE) ?: return

    server.get("/buttonPress", {
        lastTaken = LocalDateTime.now()
        response.send("OK")
    })

    server.get("/haveITakenMyPills", {
        if (lastTaken == null) {
            //Send reminder
            val headers = mapOf(
                    "Access-Token" to config.apiKey
            )
            val body =
                    mapOf(
                            "device_iden" to config.deviceId,
                            "type" to "note",
                            "title" to "Reminder: Take your pills!"
                    )

            try {
                val res = post(url = "https://api.pushbullet.com/v2/pushes", headers = headers, json = body)
                println(res.statusCode)
            } catch (e: Exception) {
                response.send(e)
            }
            response.send("No, you have not taken your pills. I sent you a reminder")

        } else {
            response.send("yes, you have taken your pills today. You took them at $lastTaken")
        }
    })

    server.get("/reset", {
        lastTaken = null
        response.send("OK")
    })

    server.start()
}
