package com.blazeloader.api.main;

import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandler {
    public static void eventCreateNewCommandManager(ReturnEventInfo<MinecraftServer, ServerCommandManager> event) {
        event.setReturnValue(BLMain.instance().getCommandHandler());
    }
}
