package net.acomputerdog.BlazeLoader.tweaklauncher;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.acomputerdog.BlazeLoader.main.Version;
import net.acomputerdog.BlazeLoader.util.BLLogger;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweakLauncher implements ITweaker {
    protected BLLogger logger = new BLLogger("BL_Launcher");
    protected List<String> tweaks = new ArrayList<String>();
    protected boolean hasInit = false;
    protected File gameDir, assetDir = null;

    public TweakLauncher(){
        logger.logInfo("BL tweak loader starting.");
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        hasInit = true;
        this.gameDir = gameDir;
        this.assetDir = assetsDir;
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        ArgumentAcceptingOptionSpec<String> optionSecondaryTweak = parser.accepts("secondaryTweaks", "Secondary tweak classes to be loaded after BL").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        OptionSet options = parser.parse(args.toArray(new String[args.size()]));
        if(options.has(optionSecondaryTweak)){
            logger.logInfo("Secondary tweaks detected.");
            tweaks = optionSecondaryTweak.values(options);
        }else{
            logger.logInfo("No secondary tweaks detected.");
        }
        Map<String, String> launchArgs = (Map<String, String>)Launch.blackboard.get("launchArgs");
        if (launchArgs == null){
            launchArgs = new HashMap<String, String>();
            Launch.blackboard.put("launchArgs", launchArgs);
        }
        if (!launchArgs.containsKey("--version")){
            launchArgs.put("--version", Version.getMinecraftVersion());
        }
        if (!launchArgs.containsKey("--gameDir") && gameDir != null){
            launchArgs.put("--gameDir", gameDir.getAbsolutePath());
        }
        if (!launchArgs.containsKey("--assetsDir") && assetDir != null){
            launchArgs.put("--assetsDir", assetDir.getAbsolutePath());
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if(hasInit){
            List<String> tweakList = (List<String>) Launch.blackboard.get("TweakClasses");
            if(tweakList != null){
                for(String tweak : tweaks){
                    try {
                        classLoader.addURL(new URL(tweak));
                        tweakList.add(tweak);
                    } catch (Exception e) {
                        logger.logError("Caught exception while injecting tweak: "+ tweak);
                        e.printStackTrace();
                    }
                }
            }else{
                logger.logFatal("tweakList is null!  Unable to inject secondary tweaks!");
            }
        }else{
            logger.logFatal("attempted to inject tweaks before scanning for other tweaks!");
        }
    }

    @Override
    /**
     * Get the main class.
     */
    public String getLaunchTarget() {
        return "net.acomputerdog.BlazeLoader.tweaklauncher.BLMain";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
