package com.blazeloader.event.handlers.client;

import net.minecraft.client.resources.model.ModelBakery;

import com.blazeloader.api.item.ItemRegistry;
import com.mumfrey.liteloader.transformers.event.EventInfo;

/**
 * Event handler for events that are not passed to mods, but rather to BL itself
 */
public class InternalEventHandlerClient {
	
    public static void eventRegisterVariantNames(EventInfo<ModelBakery> event) {
    	ItemRegistry.instance().insertItemVariantNames(event.getSource().variantNames);
    }
}