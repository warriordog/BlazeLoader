package com.blazeloader.event.transformers;

/**
 * Injects events into MC classes
 */
public class BLEventInjectionTransformerClient extends BLEventInjectionTransformer {

    /**
     * Subclasses should register events here
     */
    @Override
    protected void addBLEvents() {
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.profiler.Profiler.startSection (Ljava/lang/String;)V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.profiler.Profiler.endSection ()V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.Minecraft.displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.entity.EntityTracker.trackEntity (Lnet/minecraft/entity/Entity;)V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.entity.EntityTrackerEntry.func_151260_c ()Lnet/minecraft/network/Packet;");
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.network.NetHandlerPlayClient.handleOpenWindow (Lnet/minecraft/network/play/server/S2DPacketOpenWindow;)V");
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.multiplayer.WorldClient.func_180503_b (Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z");
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.particle.EffectRenderer.spawnEffectParticle (IDDDDDD[I)Lnet/minecraft/client/particle/EntityFX;");
        addBLEvent(EventSide.CLIENT, "net.minecraft.client.entity.EntityPlayerSP.setPlayerSPHealth (F)V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.client.ClientBrandRetriever.getClientModName ()Ljava/lang/String;", beforeReturn);
        addBLEvent(EventSide.INTERNAL_CLIENT, "net.minecraft.client.resources.model.ModelBakery.registerVariantNames ()V", beforeReturn);
    }
    
    @Override
    public String getSide() {
    	return "client";
    }
}

