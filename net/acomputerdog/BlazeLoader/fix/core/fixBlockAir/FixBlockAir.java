package net.acomputerdog.BlazeLoader.fix.core.fixBlockAir;

import net.acomputerdog.BlazeLoader.fix.Fix;
import net.minecraft.src.Material;

/**
 * Adds BlockAir with ID 0 to simplify checking for air blocks.
 */
public class FixBlockAir extends Fix {
    /**
     * Applies the fix.
     */
    @Override
    public void apply() {
        new BlockAir(0, Material.air);
    }

    /**
     * Gets the name of the fix to be displayed in debug messages.
     *
     * @return Returns the name of the fix.
     */
    @Override
    public String getFixName() {
        return "Air block fix";
    }
}
