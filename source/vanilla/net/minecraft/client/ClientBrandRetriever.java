package net.minecraft.client;

import net.acomputerdog.BlazeLoader.api.client.ApiClientWindow;

public class ClientBrandRetriever
{
    private static final String __OBFID = "CL_00001460";

    public static String getClientModName()
    {
        return ApiClientWindow.getClientBrand();
    }
}
