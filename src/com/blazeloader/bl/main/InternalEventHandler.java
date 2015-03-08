package com.blazeloader.bl.main;

import com.blazeloader.api.client.ApiWindowClient;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandler {
    public static void eventCreateNewCommandManager(ReturnEventInfo<MinecraftServer, ServerCommandManager> event) {
        event.setReturnValue(BLMain.instance().getCommandHandler());
    }

    public static void eventGetClientModName(ReturnEventInfo<ClientBrandRetriever, String> event) {
        event.setReturnValue(ApiWindowClient.getClientBrand());
    }
}
