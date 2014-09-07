package com.blazeloader.api.abstracted.base.game;

import com.blazeloader.api.abstracted.client.game.GameClient;
import com.blazeloader.api.abstracted.server.game.GameServer;

public interface Game {
    public boolean isServer();

    public boolean isClient();

    public GameServer getServer();

    public GameClient getClient();
}
