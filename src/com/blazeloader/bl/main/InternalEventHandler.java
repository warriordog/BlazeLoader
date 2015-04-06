package com.blazeloader.bl.main;

import com.blazeloader.api.ApiGeneral;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandler {
    public static void eventCreateNewCommandManager(ReturnEventInfo<MinecraftServer, CommandHandler> event) {
        event.setReturnValue(BLMain.instance().getCommandHandler());
    }

    public static void eventGetClientModName(ReturnEventInfo<ClientBrandRetriever, String> event) {
    	event.setReturnValue(retrieveBrand(event.getReturnValue()));
    }
    
    public static void eventGetServerModName(ReturnEventInfo<MinecraftServer, String> event) {
    	event.setReturnValue(retrieveBrand(event.getReturnValue()));
    }
    
    private static String retrieveBrand(String inheritedBrand) {
    	String brand = ApiGeneral.getBrand();
        if (inheritedBrand != null && !(inheritedBrand.isEmpty()  || "vanilla".contentEquals(inheritedBrand) || "LiteLoader".contentEquals(inheritedBrand))) {
        	return inheritedBrand + " / " + brand;
        }
        return brand;
    }
}
