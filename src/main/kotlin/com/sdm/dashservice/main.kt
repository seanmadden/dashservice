package com.sdm.dashservice

import org.wasabi.app.AppServer
import java.io.File
import com.google.gson.Gson;
import com.github.salomonbrys.kotson.*
import khttp.post

public data class Configuration(
        val apiKey: String,
        val deviceId: String
                    )

fun loadConfig(fileName: String): Configuration? {
    val config = Gson().fromJson<Configuration>(File(fileName).readText())

    return config
}

public fun main(args: Array<String>) {
    var server = AppServer()
    var taken = false
    val config = loadConfig("key.json") ?: return

    server.get("/buttonPress", {
        println("Received button press")
        taken = true
        response.send("OK")
    })

    server.get("/haveITakenMyPills", {
        println("taken is: $taken")
        if (!taken) {
            //Send reminder
            println("sending reminder")

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
            response.send("yes, you have taken your pills")
        }
    })

    server.get("/reset", {
        taken = false;
        response.send("OK")
    })

    server.start()
}
