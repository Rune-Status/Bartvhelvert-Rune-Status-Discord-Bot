package net.runestatus.discord.oldschool.game.net

/** Different type of server responses the server can send after creating an on demand cache session. */
enum class ServerStatus(val opcode: Int) {
    /** An opcode indicating the connection was successfully instantiated. */
    SUCCESSFUL(0),

    /** An opcode indicating the game is out of date */
    OUT_OF_DATE(6),
}