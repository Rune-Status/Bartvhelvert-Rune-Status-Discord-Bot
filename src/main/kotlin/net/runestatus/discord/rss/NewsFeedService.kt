package net.runestatus.discord.rss

import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import net.dv8tion.jda.core.EmbedBuilder
import net.runestatus.discord.DiscordBot
import net.runestatus.discord.TimedService
import java.awt.Color
import java.io.FileNotFoundException
import java.net.URL
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.concurrent.fixedRateTimer

/**
 * A [TimedService] which makes request to the rss feeds of runescape and sends the updates in to the discord chat
 * @param host The url that connects to the server
 * @param toChannel The channel in which the news feed messages will be send
 * @param color The color of the embedded message
 */
class NewsFeedService(val host: String, val toChannel: String, val color: Color) :
    TimedService {
    /** The currently stored feed */
    private var currentFeed = SyndFeedInput().build(XmlReader(URL(host)))

    override fun start(period: Long) {
        fixedRateTimer(this::class.simpleName, true, 0, period, {
            checkUpdate()
        })
    }

    /** Checks if the feed has been updated */
    private fun checkUpdate() {
        if(hasUpdated()) {
            sendUpdateMessageToChat()
        }
    }

    /** Returns whether the RSS page has been updated */
    private fun hasUpdated(): Boolean {
        val newFeed = try {
            SyndFeedInput().build(XmlReader(URL(host)))
        } catch (e: FileNotFoundException) {
            return false
        }
        return if(newFeed.entries[0].link != currentFeed.entries[0].link) {
            currentFeed = newFeed
            true
        } else {
            false
        }
    }

    /**
     * Sends the last added message of [currentFeed] to [toChannel] as an embedded message
     * @param lastNewsFeed How many news feeds since the newest one back in time should be displayed
     */
    fun sendUpdateMessageToChat(lastNewsFeed: Int = 0) {
        val entry = currentFeed.entries[lastNewsFeed]
        val messageBldr = EmbedBuilder()
            .setTitle(entry.title, entry.link)
            .setDescription(entry.description.value)
            .setColor(color)
            .setThumbnail(entry.enclosures[0].url)
        val channel = DiscordBot.discordBot.textChannels.find {it.name.contains(toChannel)}
                ?: DiscordBot.discordBot.textChannels[0]!!
        channel.sendMessage(messageBldr.build()).queue()
    }

}