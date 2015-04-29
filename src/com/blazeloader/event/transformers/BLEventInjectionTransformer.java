package com.blazeloader.event.transformers;

import com.blazeloader.bl.exception.InvalidEventException;
import com.blazeloader.bl.main.BLMain;
import com.blazeloader.bl.obf.BLMethodInfo;
import com.blazeloader.bl.obf.BLOBF;
import com.blazeloader.bl.obf.OBFLevel;
import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.BeforeReturn;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * Side-independent event injector
 */
public class BLEventInjectionTransformer extends EventInjectionTransformer {
    protected static final InjectionPoint methodHead = new MethodHead();
    protected static final InjectionPoint beforeReturn = new BeforeReturn();
    
    protected void addBLConstructorEvent(EventSide side, String clazz, Object[] paramaterTypes) {
    	addBLConstructorEvent(side, clazz, paramaterTypes, methodHead);
    }
    
    protected void addBLConstructorEvent(EventSide side, String clazz, Object[] paramaterTypes, InjectionPoint injectionPoint) {
    	BLOBF clas = BLOBF.getClass(clazz, OBFLevel.MCP);
    	String name = clas.simpleName;
    	addEvent(Event.getOrCreate("BL.<init>." + name, true), new MethodInfo(clas, Obf.constructor, Void.TYPE, paramaterTypes), injectionPoint).addListener(new MethodInfo(side.getHandler(), "init" + capitaliseFirst(name)));
    }
    
    protected void addBLEvent(EventSide side, String method, InjectionPoint injectionPoint) {
        addBLEvent(side, BLMethodInfo.create(BLOBF.getMethod(method, OBFLevel.MCP)), injectionPoint);
    }

    protected void addBLEvent(EventSide side, String method) {
        addBLEvent(side, BLMethodInfo.create(BLOBF.getMethod(method, OBFLevel.MCP)));
    }

    protected void addBLEvent(EventSide side, BLMethodInfo method) {
        addBLEvent(side, method, methodHead);
    }

    protected void addBLEvent(EventSide side, BLMethodInfo method, InjectionPoint injectionPoint) {
        if (method == null || injectionPoint == null) {
            throw new InvalidEventException(side.toString(), method, injectionPoint);
        }
        String name = method.getSimpleName();
        addEvent(Event.getOrCreate("BL." + name, true), method, injectionPoint).addListener(new MethodInfo(side.getHandler(), "event" + capitaliseFirst(name)));
    }

    protected static String capitaliseFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return String.valueOf(str.charAt(0)).toUpperCase().concat(str.substring(1, str.length()));
    }

    @Override
    protected void addEvents() {
        try {
        	addBLEvents();
        } catch (Exception e) {
        	e.printStackTrace();
            BLMain.instance().shutdown("A fatal exception occurred while injecting BlazeLoader events for side ( " + getSide().toUpperCase() + " )!\r\nThis error is not recoverable, BlazeLoader will now shut down the game.", -1);
        }
    }
    
    protected void addBLEvents() {
    	addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedIn (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedOut (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
        addBLEvent(EventSide.SERVER, "net.minecraft.server.management.ServerConfigurationManager.recreatePlayerEntity (Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;");
        addBLEvent(EventSide.INTERNAL, "net.minecraft.server.MinecraftServer.createNewCommandManager ()Lnet/minecraft/command/ServerCommandManager;", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.server.integrated.IntegratedServer.createNewCommandManager ()Lnet/minecraft/command/ServerCommandManager;", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.server.MinecraftServer.getServerModName ()Ljava/lang/String;", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.world.chunk.Chunk.populateChunk (Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.world.chunk.Chunk.onChunkLoad ()V", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.world.chunk.Chunk.onChunkUnload ()V", beforeReturn);
        
        addBLConstructorEvent(EventSide.SERVER, "net.minecraft.entity.Entity", new Object[] {BLOBF.getClass("net.minecraft.world.World", OBFLevel.MCP) }, beforeReturn);
        
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.writeToNBT (Lnet/minecraft/nbt/NBTTagCompound;)V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.readFromNBT (Lnet/minecraft/nbt/NBTTagCompound;)V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.copyDataFromOld (Lnet/minecraft/entity/Entity;)V", beforeReturn);
        addBLEvent(EventSide.INTERNAL, "net.minecraft.entity.Entity.addEntityCrashInfo (Lnet/minecraft/crash/CrashReportCategory;)V", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.Entity.entityDropItem (Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;");
        
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.EntityLiving.updateEquipmentIfNeeded (Lnet/minecraft/entity/item/EntityItem;)V");
        
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer.dropItem (Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/item/EntityItem;");
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer.dropOneItem (Z)Lnet/minecraft/entity/item/EntityItem;");
        addBLEvent(EventSide.SERVER, "net.minecraft.inventory.Container.slotClick (IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.EntityPlayer.collideWithPlayer (Lnet/minecraft/entity/Entity;)V");
        
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.InventoryPlayer.changeCurrentItem (I)V", beforeReturn);
        addBLEvent(EventSide.SERVER, "net.minecraft.entity.player.InventoryPlayer.setCurrentItem (Lnet/minecraft/item/Item;IZZ)V", beforeReturn);
        
        //Disabled because for some reason we can't transform EntityLivingBase.
        //It result in a java.lang.ClassCircularityError on EntityPlayer
        //addBLEvent(EventSide.SERVER, "net.minecraft.entity.EntityLivingBase.onItemPickup (Lnet/minecraft/entity/Entity;I)V");
    }
    
    public String getSide() {
    	return "server";
    }
    
    protected enum EventSide {
        //TODO: Reminder to update these when changing package structure.
    	CLIENT("com.blazeloader.event.handlers.client.EventHandlerClient"),
    	SERVER("com.blazeloader.event.handlers.EventHandler"),
    	INTERNAL("com.blazeloader.event.handlers.InternalEventHandler"),
    	INTERNAL_CLIENT("com.blazeloader.event.handlers.client.InternalEventHandlerClient");
    	
    	private final String handler;
    	
    	EventSide(String h) {
    		handler = h;
    	}
    	
    	public String getHandler() {
    		return handler;
    	}
    }
}
