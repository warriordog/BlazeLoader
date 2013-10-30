package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.EntityRenderer;

@Deprecated
/**
 * Proxy class for EntityRenderer.  Provides Mod.tick() and ensures that rendering done there does not flicker.
 */
public class EntityRendererProxy extends EntityRenderer {
    public EntityRendererProxy(MinecraftProxy par1Minecraft) {
        super(par1Minecraft);
    }

    /**
     * Will update any inputs that effect the camera angle (mouse) and then render the world and GUI
     */
    @Override
    public void updateCameraAndRender(float par1) {
        super.updateCameraAndRender(par1);
        ModList.tick();
    }
}
