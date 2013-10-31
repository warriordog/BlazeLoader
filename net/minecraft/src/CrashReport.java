package net.minecraft.src;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.main.Version;
import net.acomputerdog.BlazeLoader.mod.Mod;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Generates a crash report.
 */
public class CrashReport
{
    /** Description of the crash report. */
    private final String description;

    /** The Throwable that is the "cause" for this crash and Crash Report. */
    private final Throwable cause;
    private final CrashReportCategory systemDetailsCategory = new CrashReportCategory(this, "System Details");

    /** Holds the keys and values of all crash report sections. */
    private final List<CrashReportCategory> crashReportSections = new ArrayList<CrashReportCategory>();

    /** File of crash report. */
    private File crashReportFile;
    private boolean existsCrashReport = true;
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReport(String description, Throwable cause)
    {
        this.description = description;
        this.cause = cause;
        this.populateEnvironment();
    }

    /**
     * Populates this crash report with initial information about the running server and operating system / java
     * environment
     */
    private void populateEnvironment()
    {
        this.systemDetailsCategory.addCrashSectionCallable("Minecraft Version", new CallableMinecraftVersion(this));
        this.systemDetailsCategory.addCrashSectionCallable("Operating System", new CallableOSInfo(this));
        this.systemDetailsCategory.addCrashSectionCallable("Java Version", new CallableJavaInfo(this));
        this.systemDetailsCategory.addCrashSectionCallable("Java VM Version", new CallableJavaInfo2(this));
        this.systemDetailsCategory.addCrashSectionCallable("Memory", new CallableMemoryInfo(this));
        this.systemDetailsCategory.addCrashSectionCallable("JVM Flags", new CallableJVMFlags(this));
        this.systemDetailsCategory.addCrashSectionCallable("AABB Pool Size", new CallableCrashMemoryReport(this));
        this.systemDetailsCategory.addCrashSectionCallable("Suspicious classes", new CallableSuspiciousClasses(this));
        this.systemDetailsCategory.addCrashSectionCallable("IntCache", new CallableIntCache(this));
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash Report.
     */
    public Throwable getCrashCause()
    {
        return this.cause;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
    public void getSectionsInStringBuilder(StringBuilder stringBuilder)
    {
        if (this.stackTrace != null && this.stackTrace.length > 0)
        {
            stringBuilder.append("-- Head --\n");
            stringBuilder.append("Stacktrace:\n");
            StackTraceElement[] var2 = this.stackTrace;

            for (StackTraceElement var5 : var2) {
                stringBuilder.append("\t").append("at ").append(var5.toString());
                stringBuilder.append("\n");
            }

            stringBuilder.append("\n");
        }

        for (Object crashReportSection : this.crashReportSections) {
            CrashReportCategory var7 = (CrashReportCategory) crashReportSection;
            var7.func_85072_a(stringBuilder);
            stringBuilder.append("\n\n");
        }

        this.systemDetailsCategory.func_85072_a(stringBuilder);
    }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
     */
    public String getCauseStackTraceOrString()
    {
        StringWriter var1 = null;
        PrintWriter var2 = null;
        String var3 = this.cause.toString();

        try
        {
            var1 = new StringWriter();
            var2 = new PrintWriter(var1);
            this.cause.printStackTrace(var2);
            var3 = var1.toString();
        }
        finally
        {
            try
            {
                if (var1 != null)
                {
                    var1.close();
                }

                if (var2 != null)
                {
                    var2.close();
                }
            }
            catch (IOException ignored){}
        }

        return var3;
    }

    /**
     * Gets the complete report with headers, stack trace, and different sections as a string.
     */
    public String getCompleteReport()
    {
        StringBuilder var1 = new StringBuilder();
        var1.append("---- Minecraft Crash Report ----\n");
        var1.append("// ");
        var1.append(getWittyComment());
        var1.append("\n\n");
        var1.append("Time: ");
        var1.append((new SimpleDateFormat()).format(new Date()));
        var1.append("\n");
        var1.append("Description: ");
        var1.append(this.description);
        var1.append("\n\n");
        var1.append("-- BlazeLoader --\n");
        var1.append("BlazeLoader version: ").append(Version.getStringVersion()).append("\n");
        var1.append("Active mod: ").append(getActiveMod()).append("\n");
        var1.append("\n");
        var1.append(this.getCauseStackTraceOrString());
        var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int var2 = 0; var2 < 87; ++var2)
        {
            var1.append("-");
        }

        var1.append("\n\n");
        this.getSectionsInStringBuilder(var1);
        return var1.toString();
    }

    /**
     * Gets the file this crash report is saved into.
     */
    public File getFile()
    {
        return this.crashReportFile;
    }

    /**
     * Saves the complete crash report to the given File.
     */
    public boolean saveToFile(File crashReportFile, ILogAgent logger)
    {
        if (this.crashReportFile != null)
        {
            return false;
        }
        else
        {
            if (crashReportFile.getParentFile() != null)
            {
                crashReportFile.getParentFile().mkdirs();
            }

            try
            {
                FileWriter var3 = new FileWriter(crashReportFile);
                var3.write(this.getCompleteReport());
                var3.close();
                this.crashReportFile = crashReportFile;
                return true;
            }
            catch (Throwable var4)
            {
                logger.logSevereException("Could not save crash report to " + crashReportFile, var4);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory()
    {
        return this.systemDetailsCategory;
    }

    /**
     * Creates a CrashReportCategory
     */
    public CrashReportCategory makeCategory(String categoryName)
    {
        return this.makeCategoryDepth(categoryName, 1);
    }

    /**
     * Creates a CrashReportCategory for the given stack trace depth
     */
    public CrashReportCategory makeCategoryDepth(String categoryName, int depth)
    {
        CrashReportCategory var3 = new CrashReportCategory(this, categoryName);

        if (this.existsCrashReport)
        {
            int var4 = var3.func_85073_a(depth);
            StackTraceElement[] var5 = this.cause.getStackTrace();
            StackTraceElement var6 = null;
            StackTraceElement var7 = null;

            if (var5 != null && var5.length - var4 < var5.length)
            {
                var6 = var5[var5.length - var4];

                if (var5.length + 1 - var4 < var5.length)
                {
                    var7 = var5[var5.length + 1 - var4];
                }
            }

            this.existsCrashReport = var3.func_85069_a(var6, var7);

            if (var4 > 0 && !this.crashReportSections.isEmpty())
            {
                CrashReportCategory var8 = this.crashReportSections.get(this.crashReportSections.size() - 1);
                var8.func_85070_b(var4);
            }
            else if (var5 != null && var5.length >= var4)
            {
                this.stackTrace = new StackTraceElement[var5.length - var4];
                System.arraycopy(var5, 0, this.stackTrace, 0, this.stackTrace.length);
            }
            else
            {
                this.existsCrashReport = false;
            }
        }

        this.crashReportSections.add(var3);
        return var3;
    }

    /**
     * Gets a random witty comment for inclusion in this CrashReport
     */
    public static String getWittyComment()
    {
        String[] var0 = new String[] {"Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!"};

        try
        {
            return var0[(int)(System.nanoTime() % (long)var0.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }

    /**
     * Creates a crash report for the exception
     */
    public static CrashReport makeCrashReport(Throwable cause, String description)
    {
        CrashReport var2;

        if (cause instanceof ReportedException)
        {
            var2 = ((ReportedException)cause).getCrashReport();
        }
        else
        {
            var2 = new CrashReport(description, cause);
        }

        return var2;
    }

    public static String getActiveMod(){
        Mod mod = BlazeLoader.activeMod;
        if(mod != null){
            return mod.getModName() + "(" + mod.getModId() + " - " + mod.getStringModVersion() + ")";
        }else{
            return "none(null)";
        }
    }
}
