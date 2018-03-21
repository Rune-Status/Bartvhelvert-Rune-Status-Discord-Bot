package net.runestatus.discord.oldschool.game.net

/** Different type of connections a client can make to the server */
enum class ConnectionType(val opcode: Int) {
    /** An opcode to indicate the client wants to start a game session */
    GAME_CONNECTION(14),

    /** An opcode to indicate the client wants to start an on demand cache connection */
    UPDATE_CONNECTION(15);
}