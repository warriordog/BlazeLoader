/*
Copyright Mojang AB.
 */

package net.minecraft.src;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;

import java.io.File;

public class Session
{
    private final String username;
    private final String sessionId;

    public Session(String par1Str, String par2Str)
    {
        this.username = par1Str;
        this.sessionId = par2Str;
        BlazeLoader.init(new File(System.getProperty("user.dir")));
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getSessionID()
    {
        return this.sessionId;
    }
}
