package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.Profiler;

public class ProfilerProxy extends Profiler {
    private boolean hasLoadedMods = false;

    @Override
    public void startSection(String par1Str) {
        //Custom event here?
        super.startSection(par1Str);
    }

    @Override
    public void endSection() {
        //Custom event here?
        if(getNameOfLastSection().equals("root")){
            if(!hasLoadedMods){
                hasLoadedMods = true;
                ModList.startAllMods();
            }
        }
        super.endSection();
    }
}
