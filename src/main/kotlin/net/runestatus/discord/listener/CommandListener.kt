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
            "osrs" -> {
                if (commands.size == 1) return
                when(commands[1]) {
                    in UPDATE -> {
                        if(commands.size == 3 && commands[2].matches(Regex("[-+]?\\d*\\.?\\d+"))) {
                            DiscordBot.newsFeedServiceOSRS.sendUpdateMessageToChat(commands[2].toInt() % 15)
                        } else if(commands.size == 2) {
                            DiscordBot.newsFeedServiceOSRS.sendUpdateMessageToChat()
                        }
                    }
                    in REVISIONCHECK -> {
                        DiscordBot.gameServiceOSRS.updateRevision()
                        event.textChannel.sendMessage("The current OSRS revision is " +
                                "${DiscordBot.gameServiceOSRS.revision}.").queue()
                    }
                }

            }
            "rs" -> {
                if (commands.size == 1) return
                when(commands[1]) {
                    in UPDATE -> {
                        if(commands.size == 3 && commands[2].matches(Regex("[-+]?\\d*\\.?\\d+"))) {
                            DiscordBot.newsFeedServiceRS3.sendUpdateMessageToChat(commands[2].toInt() % 15)
                        } else if(commands.size == 2) {
                            DiscordBot.newsFeedServiceRS3.sendUpdateMessageToChat()
                        }
                    }
                }
            }

        }
    }

    companion object {
        /** Command aliases for revision the revision check command */
        val REVISIONCHECK = listOf("rev", "revision", "osrsrev", "osrsrevision")
        val UPDATE = listOf("update")
    }
}