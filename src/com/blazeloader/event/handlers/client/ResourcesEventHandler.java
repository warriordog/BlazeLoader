package com.blazeloader.event.handlers.client;

import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import com.blazeloader.bl.main.BlazeLoaderAPI;
import com.blazeloader.event.listeners.client.ResourcesListener;
import com.mumfrey.liteloader.common.Resources;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.resources.InternalResourcePack;

public class ResourcesEventHandler implements IResourceManagerReloadListener {
	private static ResourcesEventHandler instance;
	
	public static final HandlerList<ResourcesListener> resourcesReloaders = new HandlerList<ResourcesListener>(ResourcesListener.class);
	
	private ResourcesEventHandler() {
		instance = this;
	}
	
	protected static final ResourcesEventHandler instance() {
		return instance;
	}
	
	public static void initialiseResources(Resources<?, InternalResourcePack> resources) {
        resources.registerResourcePack(new InternalResourcePack("BlazeLoader Resources", BlazeLoaderAPI.class, "blazeloader"));
        if (resources.getResourceManager() instanceof IReloadableResourceManager) {
        	((IReloadableResourceManager)resources.getResourceManager()).registerReloadListener(new ResourcesEventHandler());
        }
	}
	
	
	public void onResourceManagerReload(IResourceManager resourceManager) {
		resourcesReloaders.all().onResourcesReloaded(resourceManager);
	}
}
