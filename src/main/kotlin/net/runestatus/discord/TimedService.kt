package net.runestatus.discord

/** An interface which has to be implemented by all services that run in the background retrieving information for the
 * bot.
 */
interface TimedService {
    /** Starts the timed service */
    fun start(period: Long)
}