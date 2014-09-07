package com.blazeloader.api.abstracted.base.game;

import com.blazeloader.api.abstracted.client.game.GameClient;
import com.blazeloader.api.abstracted.server.game.GameServer;

/**
 * Represents the game, either a client or server.
 */
public interface Game {
    /**
     * Returns true if this Game represents a Server, false if Client.
     *
     * @return return true if this Game represents a Server, false if Client.
     */
    public boolean supportsServer();

    /**
     * Returns true if this Game represents a Client, false if Server.
     *
     * @return return true if this Game represents a Client, false if Server.
     */
    public boolean supportsClient();

    /**
     * Gets the server for this game, if supported.
     *
     * @return Return the server for this game, or null if not supported.
     */
    public GameServer getServer();

    /**
     * Gets the Client for this game, if supported.
     *
     * @return Return the client for this game, or null if not supported.
     */
    public GameClient getClient();
}
