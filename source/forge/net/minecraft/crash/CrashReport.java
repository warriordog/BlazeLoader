package net.minecraft.crash;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

public class CrashReport {
    private static final Logger logger = LogManager.getLogger();
    /**
     * Description of the crash report.
     */
    private final String description;
    /**
     * The Throwable that is the "cause" for this crash and Crash Report.
     */
    private final Throwable cause;
    /**
     * Category of crash
     */
    private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
    /**
     * Holds the keys and values of all crash report sections.
     */
    private final List crashReportSections = new ArrayList();
    /**
     * File of crash report.
     */
    private File crashReportFile;
    private boolean field_85059_f = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];
    private static final String __OBFID = "CL_00000990";

    public CrashReport(String par1Str, Throwable par2Throwable) {
        this.description = par1Str;
        this.cause = par2Throwable;
        this.populateEnvironment();
    }

    /**
     * Populates this crash report with initial information about the running server and operating system / java
     * environment
     */
    private void populateEnvironment() {
        this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable() {
            private static final String __OBFID = "CL_00001197";

            public String call() {
                return "1.7.2";
            }
        });
        this.theReportCategory.addCrashSectionCallable("Operating System", new Callable() {
            private static final String __OBFID = "CL_00001222";

            public String call() {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java Version", new Callable() {
            private static final String __OBFID = "CL_00001248";

            public String call() {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable() {
            private static final String __OBFID = "CL_00001275";

            public String call() {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Memory", new Callable() {
            private static final String __OBFID = "CL_00001302";

            public String call() {
                Runtime runtime = Runtime.getRuntime();
                long i = runtime.maxMemory();
                long j = runtime.totalMemory();
                long k = runtime.freeMemory();
                long l = i / 1024L / 1024L;
                long i1 = j / 1024L / 1024L;
                long j1 = k / 1024L / 1024L;
                return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
            }
        });
        this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable() {
            private static final String __OBFID = "CL_00001329";

            public String call() {
                RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                List list = runtimemxbean.getInputArguments();
                int i = 0;
                StringBuilder stringbuilder = new StringBuilder();

                for (Object aList : list) {
                    String s = (String) aList;

                    if (s.startsWith("-X")) {
                        if (i++ > 0) {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append(s);
                    }
                }

                return String.format("%d total; %s", i, stringbuilder.toString());
            }
        });
        this.theReportCategory.addCrashSectionCallable("AABB Pool Size", new Callable() {
            private static final String __OBFID = "CL_00001355";

            public String call() {
                int i = AxisAlignedBB.getAABBPool().getlistAABBsize();
                int j = 56 * i;
                int k = j / 1024 / 1024;
                int l = AxisAlignedBB.getAABBPool().getnextPoolIndex();
                int i1 = 56 * l;
                int j1 = i1 / 1024 / 1024;
                return i + " (" + j + " bytes; " + k + " MB) allocated, " + l + " (" + i1 + " bytes; " + j1 + " MB) used";
            }
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", new Callable() {
            private static final String __OBFID = "CL_00001382";

            public String call() throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
                return IntCache.getCacheSizes();
            }
        });
        FMLCommonHandler.instance().enhanceCrashReport(this, this.theReportCategory);
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash Report.
     */
    public Throwable getCrashCause() {
        return this.cause;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
    public void getSectionsInStringBuilder(StringBuilder par1StringBuilder) {
        if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
            this.stacktrace = ArrayUtils.subarray(((CrashReportCategory) this.crashReportSections.get(0)).func_147152_a(), 0, 1);
        }

        if (this.stacktrace != null && this.stacktrace.length > 0) {
            par1StringBuilder.append("-- Head --\n");
            par1StringBuilder.append("Stacktrace:\n");
            StackTraceElement[] astacktraceelement = this.stacktrace;
            int i = astacktraceelement.length;

            for (StackTraceElement stacktraceelement : astacktraceelement) {
                par1StringBuilder.append("\t").append("at ").append(stacktraceelement.toString());
                par1StringBuilder.append("\n");
            }

            par1StringBuilder.append("\n");
        }

        for (Object crashReportSection : this.crashReportSections) {
            CrashReportCategory crashreportcategory = (CrashReportCategory) crashReportSection;
            crashreportcategory.appendToStringBuilder(par1StringBuilder);
            par1StringBuilder.append("\n\n");
        }

        this.theReportCategory.appendToStringBuilder(par1StringBuilder);
    }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
     */
    public String getCauseStackTraceOrString() {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Object object = this.cause;

        if (((Throwable) object).getMessage() == null) {
            if (object instanceof NullPointerException) {
                object = new NullPointerException(this.description);
            } else if (object instanceof StackOverflowError) {
                object = new StackOverflowError(this.description);
            } else if (object instanceof OutOfMemoryError) {
                object = new OutOfMemoryError(this.description);
            }

            ((Throwable) object).setStackTrace(this.cause.getStackTrace());
        }

        String s = object.toString();

        try {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            ((Throwable) object).printStackTrace(printwriter);
            s = stringwriter.toString();
        } finally {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
        }

        return s;
    }

    /**
     * Gets the complete report with headers, stack trace, and different sections as a string.
     */
    public String getCompleteReport() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Crash Report ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time: ");
        stringbuilder.append((new SimpleDateFormat()).format(new Date()));
        stringbuilder.append("\n");
        stringbuilder.append("Description: ");
        stringbuilder.append(this.description);
        stringbuilder.append("\n\n");
        stringbuilder.append("-- BlazeLoader --\n");
        stringbuilder.append("BlazeLoader version: ").append(Version.getStringVersion()).append("\n");
        stringbuilder.append("Active mod: ").append(getActiveMod()).append("\n");
        stringbuilder.append("\n");
        stringbuilder.append(this.getCauseStackTraceOrString());
        stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int i = 0; i < 87; ++i) {
            stringbuilder.append("-");
        }

        stringbuilder.append("\n\n");
        this.getSectionsInStringBuilder(stringbuilder);
        return stringbuilder.toString();
    }

    /**
     * Gets the file this crash report is saved into.
     */
    @SideOnly(Side.CLIENT)
    public File getFile() {
        return this.crashReportFile;
    }

    /**
     * Saves this CrashReport to the given file and returns a value indicating whether we were successful at doing so.
     */
    public boolean saveToFile(File p_147149_1_) {
        if (this.crashReportFile != null) {
            return false;
        } else {
            if (p_147149_1_.getParentFile() != null) {
                p_147149_1_.getParentFile().mkdirs();
            }

            try {
                FileWriter filewriter = new FileWriter(p_147149_1_);
                filewriter.write(this.getCompleteReport());
                filewriter.close();
                this.crashReportFile = p_147149_1_;
                return true;
            } catch (Throwable throwable) {
                logger.error("Could not save crash report to " + p_147149_1_, throwable);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory() {
        return this.theReportCategory;
    }

    /**
     * Creates a CrashReportCategory
     */
    public CrashReportCategory makeCategory(String par1Str) {
        return this.makeCategoryDepth(par1Str, 1);
    }

    /**
     * Creates a CrashReportCategory for the given stack trace depth
     */
    public CrashReportCategory makeCategoryDepth(String par1Str, int par2) {
        CrashReportCategory crashreportcategory = new CrashReportCategory(this, par1Str);

        if (this.field_85059_f) {
            int j = crashreportcategory.getPrunedStackTrace(par2);
            StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
            StackTraceElement stacktraceelement = null;
            StackTraceElement stacktraceelement1 = null;

            int idx = astacktraceelement.length - j; //Forge fix AIOOB exception.
            if (astacktraceelement != null && idx < astacktraceelement.length && idx >= 0) {
                stacktraceelement = astacktraceelement[astacktraceelement.length - j];

                if (astacktraceelement.length + 1 - j < astacktraceelement.length) {
                    stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - j];
                }
            }

            this.field_85059_f = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement, stacktraceelement1);

            if (j > 0 && !this.crashReportSections.isEmpty()) {
                CrashReportCategory crashreportcategory1 = (CrashReportCategory) this.crashReportSections.get(this.crashReportSections.size() - 1);
                crashreportcategory1.trimStackTraceEntriesFromBottom(j);
            } else if (astacktraceelement != null && astacktraceelement.length >= j) {
                this.stacktrace = new StackTraceElement[astacktraceelement.length - j];
                System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
            } else {
                this.field_85059_f = false;
            }
        }

        this.crashReportSections.add(crashreportcategory);
        return crashreportcategory;
    }

    /**
     * Gets a random witty comment for inclusion in this CrashReport
     */
    private static String getWittyComment() {
        String[] astring = new String[]{"Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!"};

        try {
            return astring[(int) (System.nanoTime() % (long) astring.length)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    /**
     * Creates a crash report for the exception
     */
    public static CrashReport makeCrashReport(Throwable par0Throwable, String par1Str) {
        CrashReport crashreport;

        if (par0Throwable instanceof ReportedException) {
            crashreport = ((ReportedException) par0Throwable).getCrashReport();
        } else {
            crashreport = new CrashReport(par1Str, par0Throwable);
        }

        return crashreport;
    }

    private static String getActiveMod() {
        Mod mod = BlazeLoader.currActiveMod;
        if (mod != null) {
            return mod.getModName() + "(" + mod.getModId() + " - " + mod.getStringModVersion() + ")";
        } else {
            return "none(null)";
        }
    }
}
