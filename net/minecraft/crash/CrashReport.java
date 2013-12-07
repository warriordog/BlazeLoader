package net.minecraft.crash;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.main.Version;
import net.acomputerdog.BlazeLoader.mod.Mod;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Generates a crash report.
 */
public class CrashReport
{

    private static final Logger logger = LogManager.getLogger();
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
    private static final String __OBFID = "CL_00000990";

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
        this.systemDetailsCategory.addCrashSectionCallable("Minecraft Version", new Callable()
        {
            private static final String __OBFID = "CL_00001197";
            public String call()
            {
                return "1.7.2";
            }
        });
        this.systemDetailsCategory.addCrashSectionCallable("Operating System", new Callable()
        {
            private static final String __OBFID = "CL_00001222";
            public String call()
            {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.systemDetailsCategory.addCrashSectionCallable("Java Version", new Callable()
        {
            private static final String __OBFID = "CL_00001248";
            public String call()
            {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }
        });
        this.systemDetailsCategory.addCrashSectionCallable("Java VM Version", new Callable()
        {
            private static final String __OBFID = "CL_00001275";
            public String call()
            {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.systemDetailsCategory.addCrashSectionCallable("Memory", new Callable()
        {
            private static final String __OBFID = "CL_00001302";
            public String call()
            {
                Runtime var1 = Runtime.getRuntime();
                long var2 = var1.maxMemory();
                long var4 = var1.totalMemory();
                long var6 = var1.freeMemory();
                long var8 = var2 / 1024L / 1024L;
                long var10 = var4 / 1024L / 1024L;
                long var12 = var6 / 1024L / 1024L;
                return var6 + " bytes (" + var12 + " MB) / " + var4 + " bytes (" + var10 + " MB) up to " + var2 + " bytes (" + var8 + " MB)";
            }
        });
        this.systemDetailsCategory.addCrashSectionCallable("JVM Flags", new Callable()
        {
            private static final String __OBFID = "CL_00001329";
            public String call()
            {
                RuntimeMXBean var1 = ManagementFactory.getRuntimeMXBean();
                List var2 = var1.getInputArguments();
                int var3 = 0;
                StringBuilder var4 = new StringBuilder();

                for (Object aVar2 : var2) {
                    String var6 = (String) aVar2;

                    if (var6.startsWith("-X")) {
                        if (var3++ > 0) {
                            var4.append(" ");
                        }

                        var4.append(var6);
                    }
                }

                return String.format("%d total; %s", Integer.valueOf(var3), var4.toString());
            }
        });
        this.systemDetailsCategory.addCrashSectionCallable("AABB Pool Size", new Callable()
        {
            private static final String __OBFID = "CL_00001355";
            public String call()
            {
                int var1 = AxisAlignedBB.getAABBPool().getlistAABBsize();
                int var2 = 56 * var1;
                int var3 = var2 / 1024 / 1024;
                int var4 = AxisAlignedBB.getAABBPool().getnextPoolIndex();
                int var5 = 56 * var4;
                int var6 = var5 / 1024 / 1024;
                return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
            }
        });
        this.systemDetailsCategory.addCrashSectionCallable("IntCache", new Callable()
        {
            private static final String __OBFID = "CL_00001382";
            public String call() throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
            {
                return IntCache.func_85144_b();
            }
        });
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
    public void getSectionsInStringBuilder(StringBuilder builder)
    {
        if ((this.stackTrace == null || this.stackTrace.length <= 0) && this.crashReportSections.size() > 0)
        {
            this.stackTrace = (StackTraceElement[])ArrayUtils.subarray((this.crashReportSections.get(0)).func_147152_a(), 0, 1);
        }

        if (this.stackTrace != null && this.stackTrace.length > 0)
        {
            builder.append("-- Head --\n");
            builder.append("Stacktrace:\n");
            StackTraceElement[] var2 = this.stackTrace;
            int var3 = var2.length;

            for (StackTraceElement var5 : var2) {
                builder.append("\t").append("at ").append(var5.toString());
                builder.append("\n");
            }

            builder.append("\n");
        }

        for (Object var7 : this.crashReportSections) {
            ((CrashReportCategory)var7).func_85072_a(builder);
            builder.append("\n\n");
        }

        this.systemDetailsCategory.func_85072_a(builder);
    }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
     */
    public String getCauseStackTraceOrString()
    {
        StringWriter var1 = null;
        PrintWriter var2 = null;
        Object var3 = this.cause;

        if (((Throwable)var3).getMessage() == null)
        {
            if (var3 instanceof NullPointerException)
            {
                var3 = new NullPointerException(this.description);
            }
            else if (var3 instanceof StackOverflowError)
            {
                var3 = new StackOverflowError(this.description);
            }
            else if (var3 instanceof OutOfMemoryError)
            {
                var3 = new OutOfMemoryError(this.description);
            }

            ((Throwable)var3).setStackTrace(this.cause.getStackTrace());
        }

        String var4 = (var3).toString();

        try
        {
            var1 = new StringWriter();
            var2 = new PrintWriter(var1);

            ((Throwable)var3).printStackTrace(var2);
            var4 = var1.toString();
        }
        finally
        {
            IOUtils.closeQuietly(var1);
            IOUtils.closeQuietly(var2);
        }

        return var4;
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
    public boolean saveToFile(File crashReportFile) {
        if (this.crashReportFile != null) {
            return false;
        } else {
            if (crashReportFile.getParentFile() != null) {
                if (!crashReportFile.getParentFile().mkdirs()) {
                    System.out.print("Could not create crash report file!");
                }
            }

            try {
                FileWriter crashReportWritere = new FileWriter(crashReportFile);
                crashReportWritere.write(this.getCompleteReport());
                crashReportWritere.close();
                this.crashReportFile = crashReportFile;
                return true;
            } catch (Throwable t) {
                logger.error("Could not save crash report to " + crashReportFile, t);
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
    public CrashReportCategory makeCategory(String categoryName) {
        return this.makeCategoryDepth(categoryName, 1);
    }

    /**
     * Creates a CrashReportCategory for the given stack trace depth
     */
    public CrashReportCategory makeCategoryDepth(String categoryName, int depth) {
        CrashReportCategory var3 = new CrashReportCategory(this, categoryName);

        if (this.existsCrashReport) {
            int var4 = var3.func_85073_a(depth);
            StackTraceElement[] var5 = this.cause.getStackTrace();
            StackTraceElement var6 = null;
            StackTraceElement var7 = null;

            if (var5 != null && var5.length - var4 < var5.length) {
                var6 = var5[var5.length - var4];

                if (var5.length + 1 - var4 < var5.length) {
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
        Mod mod = BlazeLoader.currActiveMod;
        if(mod != null){
            return mod.getModName() + "(" + mod.getModId() + " - " + mod.getStringModVersion() + ")";
        }else{
            return "none(null)";
        }
    }
}