package net.acomputerdog.BlazeLoader.mod;

import net.minecraft.client.resources.FileResourcePack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BLModResourcePack extends FileResourcePack {
    public ModData mod;
    private final byte[] bytes;

    public BLModResourcePack(ModData mod) {
        super(mod.getModSource());
        this.mod = mod;
        this.bytes = (
            "{\n" +
            " \"pack\": {\n" +
            "   \"description\": \"Generated pack for Mod " + this.mod.getModId() + "\",\n" +
            "   \"pack_format\": 1\n" +
            "}\n" +
            "}").getBytes();
    }

    @Override
    public String getPackName() {
        return "BLModResourcePack:" + this.mod.getModId();
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException {
        if (name.equals("pack.mcmeta") && !super.hasResourceName("pack.mcmeta")) {
            return new ByteArrayInputStream(bytes);
        } else {
            return super.getInputStreamByName(name);
        }
    }
}