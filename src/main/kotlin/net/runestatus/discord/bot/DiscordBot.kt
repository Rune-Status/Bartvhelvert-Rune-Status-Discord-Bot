package net.runestatus.discord.bot

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.OnlineStatus
import net.runestatus.discord.bot.listener.CommandListener
import javax.security.auth.login.LoginException
import com.moandjiezana.toml.Toml
import java.io.File

fun main(args: Array<String>) {
    DiscordBot.start()
}

object DiscordBot {
    /** The JDA bot instance */
    lateinit var discordBot: JDA

    /** Starts the Discord bot service */
    fun start() {
        val config = readConfig("./resources/config.toml")
        startBot(config.token, config.status)
    }

    /** Reads a configuration .toml file */
    private fun readConfig(path: String): BotConfig {
        val settings = Toml().read(File(path)).getTable("settings")
        return BotConfig(
            token = settings.getString("token"),
            status = OnlineStatus.fromKey(settings.getString("status").toLowerCase())
        )
    }

    /** Initializes the JDA bot with a given [token] and [status] */
    @Throws(LoginException::class, InterruptedException::class, IllegalArgumentException::class)
    private fun startBot(token: String, status: OnlineStatus) {
        discordBot = JDABuilder(AccountType.BOT)
            .setToken(token)
            .setAutoReconnect(true)
            .setStatus(status)
            .addEventListener(CommandListener())
            .buildBlocking()
    }
}

