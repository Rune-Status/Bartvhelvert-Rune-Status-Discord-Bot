package net.runestatus.discord.bot.listener

import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class CommandListener: ListenerAdapter() {
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
                event.textChannel.sendMessage("Doing a revision check").queue()
            }
        }
    }

    companion object {
        val REVISIONCHECK = listOf("rev", "revision", "osrsrev", "osrsrevision")
    }
}