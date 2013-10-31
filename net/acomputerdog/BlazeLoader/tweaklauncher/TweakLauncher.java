package net.acomputerdog.BlazeLoader.tweaklauncher;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.acomputerdog.BlazeLoader.main.Version;
import net.acomputerdog.BlazeLoader.util.logger.BLLogger;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A launch class that allows BlazeLoader to be loaded without modifications to the jar.  Requires LaunchWrapper.
 */
public class TweakLauncher implements ITweaker {
    protected BLLogger logger = new BLLogger("BL_Launcher");
    protected List<String> tweaks = new ArrayList<String>();
    protected boolean hasInit = false;
    protected File gameDir, assetDir = null;
    protected Map<String, String> requiredArgs;
    protected List<String> handledArgs = new ArrayList<String>();
    protected List<String> ignoredArgs;
    private String username = "";
    private String session = "";

    public TweakLauncher(){
        logger.logInfo("BL tweak loader starting.");
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        int usernameIndex = args.indexOf("--username");
        if(usernameIndex != -1 && usernameIndex < args.size()){
            username = args.get(usernameIndex + 1);
        }
        int sessionIndex = args.indexOf("--session");
        if(sessionIndex != -1 && sessionIndex < args.size()){
            session = args.get(sessionIndex + 1);
        }
        hasInit = true;
        this.gameDir = gameDir;
        this.assetDir = assetsDir;
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        ArgumentAcceptingOptionSpec<String> optionSecondaryTweak = parser.accepts("secondaryTweaks", "Secondary tweak classes to be loaded after BL").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        OptionSet options = parser.parse(args.toArray(new String[args.size()]));

        NonOptionArgumentSpec<String> invalidOptions = parser.nonOptions();
        this.ignoredArgs = options.valuesOf(invalidOptions);

        if(options.has(optionSecondaryTweak)){
            logger.logInfo("Secondary tweaks detected.");
            tweaks = optionSecondaryTweak.values(options);
        }else{
            logger.logInfo("No secondary tweaks detected.");
        }

        this.parseArgs(this.ignoredArgs);

        requiredArgs = (Map<String, String>)Launch.blackboard.get("launchArgs");
        if (requiredArgs == null){
            requiredArgs = new HashMap<String, String>();
            Launch.blackboard.put("launchArgs", requiredArgs);
        }
        if (!requiredArgs.containsKey("--version")){
            requiredArgs.put("--version", Version.getMinecraftVersion());
        }
        if (!requiredArgs.containsKey("--gameDir") && gameDir != null){
            requiredArgs.put("--gameDir", gameDir.getAbsolutePath());
        }
        if (!requiredArgs.containsKey("--assetsDir") && assetDir != null){
            requiredArgs.put("--assetsDir", assetDir.getAbsolutePath());
        }
        if(!requiredArgs.containsKey("--username")){
            requiredArgs.put("--username", username);
        }
        if(!requiredArgs.containsKey("--session")){
            requiredArgs.put("--session", session);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if(hasInit){
            List<String> tweakList = (List<String>) Launch.blackboard.get("TweakClasses");
            if(tweakList != null){
                for(String tweak : tweaks){
                    try {
                        tweakList.add(tweak);
                    } catch (Exception e) {
                        logger.logError("Caught exception while injecting tweak: "+ tweak);
                        e.printStackTrace();
                    }
                }
                classLoader.registerTransformer("net.acomputerdog.BlazeLoader.tweaklauncher.BLTransformer");
            }else{
                logger.logFatal("tweakList is null!  Unable to inject secondary tweaks!");
            }
        }else{
            logger.logFatal("attempted to inject tweaks before scanning for other tweaks!");
        }
        
        classLoader.registerTransformer("net.acomputerdog.BlazeLoader.ams.AccessTransformer");
    }

    @Override
    /**
     * Get the main class.
     */
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        List<String> args = new ArrayList<String>();
        for (String arg : this.handledArgs){
            args.add(arg);
        }
        for (Map.Entry<String, String> arg : this.requiredArgs.entrySet()){
            args.add(arg.getKey().trim());
            args.add(arg.getValue().trim());
        }
        this.handledArgs.clear();
        this.requiredArgs.clear();

        return args.toArray(new String[args.size()]);
    }

    private void parseArgs(List<String> args)
    {
        String argCategoryName = null;

        for (String arg : args){
            if (arg.startsWith("-")){
                if (argCategoryName != null){
                    this.requiredArgs.put(argCategoryName, "");
                }else if (arg.contains("=")){
                    this.requiredArgs.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
                }else{
                    argCategoryName = arg;
                }
            }else{
                if (argCategoryName != null){
                    this.requiredArgs.put(argCategoryName, arg);
                }else{
                    this.handledArgs.add(arg);
                }
            }
        }
    }

    /*
    private void parseArgs(List<String> args)
    {
        String classifier = null;
        for(String arg : args){
            if(arg.startsWith("-")){
                if(classifier != null){
                    classifier = this.addClassifiedArg(classifier, "");
                }else if(arg.contains("=")){
                    classifier = this.addClassifiedArg(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
                }else{
                    classifier = arg;
                }
            }else{
                if(classifier != null){
                    classifier = this.addClassifiedArg(classifier, arg);
                }else{
                    this.handledArgs.add(arg);
                }
            }
        }
    }

    private String addClassifiedArg(String classifiedArg, String arg)
    {
        this.requiredArgs.put(classifiedArg, arg);
        return null;
    }
    */
}
