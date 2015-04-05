package com.blazeloader.api.client.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class ApiPlayer {
    public static EntityPlayerSP getPlayerEntity() {
        return Minecraft.getMinecraft().thePlayer;
    }
}
