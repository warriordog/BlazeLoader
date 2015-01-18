package com.blazeloader.api.client.api.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Contains functions related to game rendering.
 */
public class ApiRenderClient {
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static int lastScale = -1;
    private static ScaledResolution scale = null;

    /**
     * Generates a FontRenderer-compatible ARGB color int.
     *
     * @param alpha Alpha-channel value.
     * @param red   Red-channel value.
     * @param green Green-channel value.
     * @param blue  Blue-channel value.
     * @return Return the alpha, red, green, and blue compressed into an ARGB int.
     */
    public static int getARGB(int alpha, int red, int green, int blue) {
        int arbg = 0;
        arbg |= (alpha & 255) << 24;
        arbg |= (red & 255) << 16;
        arbg |= (green & 255) << 8;
        arbg |= (blue & 255);
        return arbg;
    }

    /**
     * Draws a string onto the screen.
     *
     * @param string   The string to draw.
     * @param x        The X-coord to display at.
     * @param y        The Y-coord to display at.
     * @param color    The color to display.  Should be an ARBG returned from getARBG()
     * @param shadow   Render a shadow behind the text.
     * @param centered Center the text around the coordinates specified.
     */
    public static void drawString(String string, int x, int y, int color, boolean shadow, boolean centered) {
        FontRenderer render = Minecraft.getMinecraft().fontRendererObj;
        if (centered) {
            x -= render.getStringWidth(string) / 2;
        }
        render.drawString(string, x, y, color, shadow);
    }

    /**
     * Draws a string onto the screen, without centering it.
     *
     * @param string The string to draw.
     * @param x      The X-coord to display at.
     * @param y      The Y-coord to display at.
     * @param color  The color to display.  Should be an ARBG returned from getARBG()
     * @param shadow Render a shadow behind the text.
     */
    public static void drawString(String string, int x, int y, int color, boolean shadow) {
        drawString(string, x, y, color, shadow, false);
    }

    /**
     * Draws a string onto the screen, without centering it and without a shadow.
     *
     * @param string The string to draw.
     * @param x      The X-coord to display at.
     * @param y      The Y-coord to display at.
     * @param color  The color to display.  Should be an ARBG returned from getARBG()
     */
    public static void drawString(String string, int x, int y, int color) {
        drawString(string, x, y, color, false, false);
    }

    /**
     * Draws a rectangle onto the screen.
     *
     * @param x      The x-coordinate
     * @param y      The y-coordinate
     * @param width  The width
     * @param height The height
     * @param color  The ARGB color
     */
    public static void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    /**
     * Draws a square onto the screen.
     *
     * @param x     The x-coordinate
     * @param y     The y-coordinate
     * @param size  The length of each side
     * @param color The ARGB color
     */
    public static void drawSquare(int x, int y, int size, int color) {
        drawRect(x, y, size, size, color);
    }

    /**
     * Draws a horizontal line onto the screen.
     *
     * @param x      The x-coordinate
     * @param y      The y-coordinate
     * @param length The length
     * @param color  The ARGB color
     */
    public static void drawHLine(int x, int y, int length, int color) {
        drawRect(x, y, length, 1, color);
    }

    /**
     * Draws a vertical line onto the screen.
     *
     * @param x      The x-coordinate
     * @param y      The y-coordinate
     * @param length The length
     * @param color  The ARGB color
     */
    public static void drawVLine(int x, int y, int length, int color) {
        drawRect(x, y, 1, length, color);
    }

    /**
     * Draws a point on the screen
     *
     * @param x     The x-coordinate
     * @param y     The y-coordinate
     * @param color The ARGB color
     */
    public static void drawPoint(int x, int y, int color) {
        drawRect(x, y, 1, 1, color);
    }

    /**
     * Gets the scaled width of the screen
     *
     * @return return the scaled width of the screen as returned by a ScaledResolution
     */
    public static int getScaledWidth() {
        return getScaledResolution().getScaledWidth();
    }

    /**
     * Gets the scaled height of the screen
     *
     * @return return the scaled height of the screen as returned by a ScaledResolution
     */
    public static int getScaledHeight() {
        return getScaledResolution().getScaledWidth();
    }

    /**
     * Gets a shared, updated instance of ScaledResolution that can be used to get screen scale
     *
     * @return Return a scaled, correct instance of ScaledResolution
     */
    public static ScaledResolution getScaledResolution() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (scale == null || minecraft.displayWidth != lastWidth || minecraft.displayHeight != lastHeight || minecraft.gameSettings.guiScale != lastScale) {
            lastWidth = minecraft.displayWidth;
            lastHeight = minecraft.displayHeight;
            lastScale = minecraft.gameSettings.guiScale;
            scale = new ScaledResolution(minecraft, lastWidth, lastHeight);
        }
        return scale;
    }
}
