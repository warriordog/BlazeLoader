package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.fix.FixManager;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.Profiler;

public class ProfilerProxy extends Profiler {
    private boolean hasLoadedMods = false;

    @Override
    public void startSection(String par1Str) {
        if("root".equals(par1Str)){
            if(!hasLoadedMods){
                try{
                    BlazeLoader.log("Applying fixes...");
                    FixManager.onStart();
                    BlazeLoader.log("Applied fixes with no issues.");
                }catch(Exception e){
                    BlazeLoader.log("Exception occurred while applying fixes!");
                    e.printStackTrace();
                }
                hasLoadedMods = true;
                ModList.start();
            }
        }
        ModList.startSection(par1Str);
        super.startSection(par1Str);
    }

    @Override
    public void endSection() {
        super.endSection();
        String section = super.getNameOfLastSection();
        ModList.endSection(section);
    }
}
