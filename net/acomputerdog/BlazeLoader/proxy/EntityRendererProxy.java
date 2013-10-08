package net.acomputerdog.BlazeLoader.proxy;

import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Minecraft;

public class EntityRendererProxy extends EntityRenderer {
    public EntityRendererProxy(Minecraft par1Minecraft) {
        super(par1Minecraft);
    }

    /**
     * Updates the entity renderer
     */
    @Override
    public void updateRenderer() {
        super.updateRenderer();
    }
}
