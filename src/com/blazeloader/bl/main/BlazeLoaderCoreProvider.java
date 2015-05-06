package com.blazeloader.bl.main;

import com.blazeloader.event.handlers.client.EventHandlerClient;
import com.blazeloader.event.handlers.client.ResourcesEventHandler;
import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.common.Resources;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.resources.InternalResourcePack;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.world.World;

/**
 * BlazeLoader CoreProvider
 */
public class BlazeLoaderCoreProvider implements CoreProvider {
    public static final BlazeLoaderCoreProvider instance = new BlazeLoaderCoreProvider();

    private GameEngine gameEngine;

    public GameEngine getGameEngine() {
        return gameEngine;
    }
    
    private BlazeLoaderCoreProvider() {
    }

    @Override
    public void onInit() {
    }

    /**
     * During the postInit phase, the mods which were discovered during preInit phase are initialised and the
     * interfaces are allocated. This callback is invoked at the very start of the postInit phase, before mods
     * are initialised but after the point at which it is safe to assume it's ok to access game classes. This
     * is the first point at which the Minecraft game instance should be referenced. Be aware that certain game
     * classes (such as the EntityRenderer) are NOT initialised at this point.
     *
     * @param engine Game engine
     */
    @Override
    public void onPostInit(GameEngine<?, ?> engine) {
        gameEngine = engine;
        if (engine.isClient()) {
        	ResourcesEventHandler.initialiseResources((Resources<?, InternalResourcePack>)engine.getResources());
        }
    }

    /**
     * Once the mods are initialised and the interfaces have been allocated, this callback is invoked to allow
     * the CoreProvider to perform any tasks which should be performed in the postInit phase but after mods
     * have been initialised.
     *
     * @param mods Mods that have been loaded
     */
    @Override
    public void onPostInitComplete(LiteLoaderMods mods) {

    }

    /**
     * Called once startup is complete and the game loop begins running. This callback is invoked immediately
     * prior to the first "tick" event and immediately AFTER the the "late init" phase for mods (InitCompleteListener)
     */
    @Override
    public void onStartupComplete() {
        EventHandlerClient.eventStart();
    }

    /**
     * Called immediately on joining a single or multi-player world when the JoinGame packet is received. Only called on the client.
     *
     * @param netHandler  Network handler
     * @param loginPacket Login packet
     */
    @Override
    public void onJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket) {
        EventHandlerClient.overrideClientJoinGame(netHandler, loginPacket);
    }

    @Override
    public void onPostRender(int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    public void onShutDown() {
        EventHandlerClient.eventEnd();
    }

    @Override
    public void onTick(boolean clock, float partialTicks, boolean inGame) {
        EventHandlerClient.eventTick();
    }

    @Override
    public void onWorldChanged(World world) {
        EventHandlerClient.overrideWorldChanged(world);
    }
}
