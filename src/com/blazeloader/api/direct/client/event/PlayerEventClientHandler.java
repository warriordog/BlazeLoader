package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.mod.BLMod;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;

/**
 * Interface for mods that handle player events
 */
public interface PlayerEventClientHandler extends BLMod {

    /**
     * Called when the client player dies.
     */
    public void eventClientPlayerDeath();

    /**
     * Called when the client connects to a server or singleplayer game
     *
     * @param netHandler  The network handler processing loginPacket
     * @param loginPacket The login packet for this login
     */
    public void eventClientJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket);
}
