package net.minecraft.profiler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.acomputerdog.BlazeLoader.mod.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Profiler {
    private static final Logger logger = LogManager.getLogger();
    /**
     * List of parent sections
     */
    private final List sectionList = new ArrayList();
    /**
     * List of timestamps (System.nanoTime)
     */
    private final List timestampList = new ArrayList();
    /**
     * Flag profiling enabled
     */
    public boolean profilingEnabled;
    /**
     * Current profiling section
     */
    private String profilingSection = "";
    /**
     * Profiling map
     */
    private final Map profilingMap = new HashMap();
    private static final String __OBFID = "CL_00001497";

    /**
     * Clear profiling.
     */
    public void clearProfiling() {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }

    /**
     * Start section
     */
    public void startSection(String par1Str) {
        ModList.startSection(par1Str);
        if (this.profilingEnabled) {
            if (this.profilingSection.length() > 0) {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + par1Str;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(System.nanoTime());
        }
    }

    /**
     * End section
     */
    public void endSection() {
        if (this.profilingEnabled) {
            long i = System.nanoTime();
            long j = (Long) this.timestampList.remove(this.timestampList.size() - 1);
            this.sectionList.remove(this.sectionList.size() - 1);
            long k = i - j;

            if (this.profilingMap.containsKey(this.profilingSection)) {
                this.profilingMap.put(this.profilingSection, ((Long) this.profilingMap.get(this.profilingSection)).longValue() + k);
            } else {
                this.profilingMap.put(this.profilingSection, k);
            }

            if (k > 100000000L) {
                logger.warn("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double) k / 1000000.0D + " ms");
            }

            this.profilingSection = !this.sectionList.isEmpty() ? (String) this.sectionList.get(this.sectionList.size() - 1) : "";
        }
        ModList.endSection(getNameOfLastSection());
    }

    /**
     * Get profiling data
     */
    public List getProfilingData(String par1Str) {
        if (!this.profilingEnabled) {
            return null;
        } else {
            long i = this.profilingMap.containsKey("root") ? (Long) this.profilingMap.get("root") : 0L;
            long j = this.profilingMap.containsKey(par1Str) ? (Long) this.profilingMap.get(par1Str) : -1L;
            ArrayList arraylist = new ArrayList();

            if (par1Str.length() > 0) {
                par1Str = par1Str + ".";
            }

            long k = 0L;

            for (Object o : this.profilingMap.keySet()) {
                String s1 = (String) o;

                if (s1.length() > par1Str.length() && s1.startsWith(par1Str) && s1.indexOf(".", par1Str.length() + 1) < 0) {
                    k += (Long) this.profilingMap.get(s1);
                }
            }

            float f = (float) k;

            if (k < j) {
                k = j;
            }

            if (i < k) {
                i = k;
            }

            Iterator iterator1 = this.profilingMap.keySet().iterator();
            String s2;

            while (iterator1.hasNext()) {
                s2 = (String) iterator1.next();

                if (s2.length() > par1Str.length() && s2.startsWith(par1Str) && s2.indexOf(".", par1Str.length() + 1) < 0) {
                    long l = (Long) this.profilingMap.get(s2);
                    double d0 = (double) l * 100.0D / (double) k;
                    double d1 = (double) l * 100.0D / (double) i;
                    String s3 = s2.substring(par1Str.length());
                    arraylist.add(new Profiler.Result(s3, d0, d1));
                }
            }

            iterator1 = this.profilingMap.keySet().iterator();

            while (iterator1.hasNext()) {
                s2 = (String) iterator1.next();
                this.profilingMap.put(s2, ((Long) this.profilingMap.get(s2)).longValue() * 999L / 1000L);
            }

            if ((float) k > f) {
                arraylist.add(new Profiler.Result("unspecified", (double) ((float) k - f) * 100.0D / (double) k, (double) ((float) k - f) * 100.0D / (double) i));
            }

            Collections.sort(arraylist);
            arraylist.add(0, new Profiler.Result(par1Str, 100.0D, (double) k * 100.0D / (double) i));
            return arraylist;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String par1Str) {
        this.endSection();
        this.startSection(par1Str);
    }

    public String getNameOfLastSection() {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String) this.sectionList.get(this.sectionList.size() - 1);
    }

    public static final class Result implements Comparable {
        public double field_76332_a;
        public double field_76330_b;
        public String field_76331_c;
        private static final String __OBFID = "CL_00001498";

        public Result(String par1Str, double par2, double par4) {
            this.field_76331_c = par1Str;
            this.field_76332_a = par2;
            this.field_76330_b = par4;
        }

        public int compareTo(Profiler.Result par1ProfilerResult) {
            return par1ProfilerResult.field_76332_a < this.field_76332_a ? -1 : (par1ProfilerResult.field_76332_a > this.field_76332_a ? 1 : par1ProfilerResult.field_76331_c.compareTo(this.field_76331_c));
        }

        @SideOnly(Side.CLIENT)
        public int func_76329_a() {
            return (this.field_76331_c.hashCode() & 11184810) + 4473924;
        }

        public int compareTo(Object par1Obj) {
            return this.compareTo((Profiler.Result) par1Obj);
        }
    }
}
