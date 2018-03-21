package net.runestatus.discord.listener

import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.runestatus.discord.DiscordBot

/** Listens to commands from the users. */
class CommandListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val commands = event.message.contentRaw!!.let {
            if(it.startsWith("::")) {
                it.substring(2, it.length).toLowerCase().split(" ")
            } else {
                return
            }
        }
        when(commands[0]) {
            in REVISIONCHECK -> {
                DiscordBot.gameServiceOSRS.updateRevision()
                event.textChannel.sendMessage("The current OSRS revision is ${DiscordBot.gameServiceOSRS.revision}")
                    .queue()
            }
        }
    }

    companion object {
        /** Command aliases for revision the revision check command */
        val REVISIONCHECK = listOf("rev", "revision", "osrsrev", "osrsrevision")
    }
}