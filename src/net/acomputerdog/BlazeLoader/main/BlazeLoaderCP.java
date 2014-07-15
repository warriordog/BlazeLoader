package net.acomputerdog.BlazeLoader.main;

import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.resources.InternalResourcePack;
import net.acomputerdog.BlazeLoader.event.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.world.World;

public class BlazeLoaderCP implements CoreProvider {
    public static final BlazeLoaderCP instance = new BlazeLoaderCP();

    private BlazeLoaderCP() {}


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
     * @param engine
     */
    @Override
    public void onPostInit(GameEngine<?, ?> engine) {
        engine.registerResourcePack(new InternalResourcePack("BlazeLoader Resources", BlazeLoaderAPI.class, "BlazeLoader"));
    }

    /**
     * Once the mods are initialised and the interfaces have been allocated, this callback is invoked to allow
     * the CoreProvider to perform any tasks which should be performed in the postInit phase but after mods
     * have been initialised.
     *
     * @param mods
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
        EventHandler.eventStart();
    }

    /**
     * Called immediately on joining a single or multi-player world when the JoinGame packet is received. Only called on the client.
     *
     * @param netHandler
     * @param loginPacket
     */
    @Override
    public void onJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket) {

    }

    @Override
    public void onPostRender(int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    public void onShutDown() {
        EventHandler.eventEnd();
    }

    @Override
    public void onTick(boolean clock, float partialTicks, boolean inGame) {

    }

    @Override
    public void onWorldChanged(World world) {

    }
}
