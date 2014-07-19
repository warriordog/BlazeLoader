package net.acomputerdog.BlazeLoader.util.render;

/**
 * Utilities to assist with using colors in game
 */
public class ColorUtils {
    /**
     * Generates a FontRenderer-compatible ARBG color int.
     *
     * @param alpha Alpha-channel value.
     * @param red   Red-channel value.
     * @param green Green-channel value.
     * @param blue  Blue-channel value.
     * @return Return the alpha, red, green, and blue compressed into an ARBG int.
     */
    public static int getARBG(int alpha, int red, int green, int blue) {
        int arbg = 0;
        arbg |= (alpha & 255) << 24;
        arbg |= (red & 255) << 16;
        arbg |= (blue & 255) << 8;
        arbg |= (green & 255);
        return arbg;
    }
}
