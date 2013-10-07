package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.fix.FixManager;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.Profiler;

public class ProfilerProxy extends Profiler {
    private boolean hasLoadedMods = false;

    @Override
    public void startSection(String par1Str) {
        ModList.startSection(par1Str);
        super.startSection(par1Str);
    }

    @Override
    public void endSection() {
        super.endSection();
        String sectionName = getNameOfLastSection();
        ModList.endSection(sectionName);
        if("root".equals(sectionName)){
            if(!hasLoadedMods){
                hasLoadedMods = true;
                FixManager.onStart();
                ModList.start();
            }
        }
    }
}
