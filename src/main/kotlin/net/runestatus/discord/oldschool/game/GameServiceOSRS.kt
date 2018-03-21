package net.runestatus.discord.oldschool.game

import net.runestatus.discord.TimedService
import net.runestatus.discord.oldschool.game.net.ConnectionType
import net.runestatus.discord.oldschool.game.net.ServerStatus
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.concurrent.fixedRateTimer

/**
 * A [TimedService] which makes request to the game server to retrieve useful information.
 * @param host The url that connects to the server
 * @param port The port the server listens to
 * @param revision The initial revision which the bot should check
 */
class GameServiceOSRS(private val host: String, private val port: Int, var revision: Int) : TimedService {
    override fun start(period: Long) {
        fixedRateTimer(this::class.simpleName, true, 0, period, {
            updateRevision()
        })
    }

    /** Updates the current revision if needed . The revision is found by brute-forcing starting from [revision]*/
    fun updateRevision() {
        val response = run {
            val buffer = ByteBuffer.allocate(5)!!.let {
                it.put(ConnectionType.UPDATE_CONNECTION.opcode.toByte())
                it.putInt(revision)
            }
            val socket = Socket(host, port)
            val input = socket.getInputStream()!!
            val output = socket.getOutputStream()!!
            try {
                output.write(buffer.array())
                output.flush()
                input.read()
            } finally {
                input.close()
                output.close()
            }
        }
        when (response) {
            ServerStatus.SUCCESSFUL.opcode -> return
            ServerStatus.OUT_OF_DATE.opcode -> {
                revision++
                updateRevision()
            }
        }
    }
}