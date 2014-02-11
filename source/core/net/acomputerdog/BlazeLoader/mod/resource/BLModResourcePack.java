package net.acomputerdog.BlazeLoader.mod.resource;

import net.acomputerdog.BlazeLoader.mod.ModData;
import net.minecraft.client.resources.FileResourcePack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BLModResourcePack extends FileResourcePack
{
    public ModData mod;

    public BLModResourcePack(ModData mod)
    {
        super(mod.getModSource());
        this.mod = mod;
    }

    @Override
    public String getPackName()
    {
        return "BLModResourcePack:" + this.mod.getModId();
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException
    {
        try
        {
            return super.getInputStreamByName(name);
        }
        catch (IOException e)
        {
            if (name.equals("pack.mcmeta"))
            {
                return new ByteArrayInputStream(("{\n" +
                        " \"pack\": {\n" +
                        "   \"description\": \"default pack.mcmeta generated by BL for " + this.mod.getModId() + "\",\n" +
                        "   \"pack_format\": 1\n" +
                        "}\n" +
                        "}").getBytes());
            }
            else
                throw e;
        }
    }
}