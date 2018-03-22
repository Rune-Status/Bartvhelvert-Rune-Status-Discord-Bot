package net.runestatus.discord

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.OnlineStatus
import net.runestatus.discord.listener.CommandListener
import com.moandjiezana.toml.Toml
import net.runestatus.discord.oldschool.game.GameServiceOSRS
import net.runestatus.discord.rss.NewsFeedService
import java.awt.Color
import java.io.File

fun main(args: Array<String>) {
    DiscordBot.start()
}

object DiscordBot {
    /** Absolute path of the configuration file */
    private const val configDir = "./resources/config.toml"

    /** The JDA bot instance */
    lateinit var discordBot: JDA

    /** The game service which pings the OSRS server*/
    lateinit var gameServiceOSRS: GameServiceOSRS

    lateinit var newsFeedServiceRS3: NewsFeedService

    /** The game service which pings the OSRS server*/
    lateinit var newsFeedServiceOSRS: NewsFeedService

    /** Starts the Discord bot service */
    fun start() {
        startOSRSGameService(configDir)
        startOSRSNewsFeedService(configDir)
        startsRS3NewsFeedService(configDir)
        startBot(configDir)
    }

    /** Initializes the JDA bot with a given [token] and [status] */
    private fun startBot(path: String) {
        val settings = Toml().read(File(path)).getTable("settings")
        discordBot = JDABuilder(AccountType.BOT)
            .setToken(settings.getString("token"))
            .setAutoReconnect(true)
            .setStatus(OnlineStatus.fromKey(settings.getString("status").toLowerCase()))
            .addEventListener(CommandListener())
            .buildBlocking()
    }

    /** Reads an OSRS game service configuration .toml file and start the game service */
    private fun startOSRSGameService(path: String) {
        val settings = Toml().read(File(path)).getTable("osrs.game_service")
        gameServiceOSRS = GameServiceOSRS(
            host = "oldschool${settings.getLong("world").toInt()}.runescape.com",
            port = settings.getLong("port").toInt(),
            revision = settings.getLong("default_rev").toInt()
        )
        gameServiceOSRS.start(settings.getLong("ping_period_ms"))
    }

    private fun startsRS3NewsFeedService(path: String) {
        val settings = Toml().read(File(path)).getTable("rs3.rss_service")
        newsFeedServiceRS3 = NewsFeedService(
            host = settings.getString("host"),
            toChannel = settings.getString("channel"),
            color = Color.ORANGE
        )
        newsFeedServiceRS3.start(settings.getLong("ping_period_ms"))
    }

    /** Reads an OSRS news feed configuration .toml file and start the news feed service */
    private fun startOSRSNewsFeedService(path: String) {
        val settings = Toml().read(File(path)).getTable("osrs.rss_service")
        newsFeedServiceOSRS = NewsFeedService(
            host = settings.getString("host"),
            toChannel = settings.getString("channel"),
            color = Color.RED
        )
        newsFeedServiceOSRS.start(settings.getLong("ping_period_ms"))
    }
}

