package net.acomputerdog.BlazeLoader.api.render;

import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;

/**
 * Contains functions related to game rendering.
 */
public class ApiRender {

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
        FontRenderer render = ApiGeneral.theMinecraft.fontRenderer;
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
     * Registers an entity renderer, overriding it if it exists.
     *
     * @param entity   The class of the entity to register the renderer for.
     * @param renderer The render to register.
     */
    public static void registerEntityRenderer(Class entity, Render renderer) {
    	renderer.setRenderManager(RenderManager.instance);
        BlazeLoader.getEntityRenderMap().put(entity, renderer);
    }
}
