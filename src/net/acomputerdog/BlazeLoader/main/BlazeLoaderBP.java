package net.acomputerdog.BlazeLoader.main;

import com.mumfrey.liteloader.api.BrandingProvider;
import com.mumfrey.liteloader.client.util.render.IconAbsolute;
import net.acomputerdog.BlazeLoader.util.render.ColorUtils;
import net.acomputerdog.BlazeLoader.version.Version;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import java.net.URI;
import java.net.URL;

public class BlazeLoaderBP implements BrandingProvider {
    public static final BlazeLoaderBP instance = new BlazeLoaderBP();

    private boolean hasInit = false;
    private ResourceLocation twitterLoc;
    private IIcon twitterIcon;

    private BlazeLoaderBP(){}

    private void init() {
        if (!hasInit) {
            hasInit = true;
            twitterLoc = new ResourceLocation("BlazeLoader", "tex/twitter_logo.png");
            twitterIcon = new IconAbsolute(twitterLoc, "twitter", 32, 32, 0, 0, 32, 32, 32);
        }
    }

    /**
     * Get the priority of this provider, higher numbers take precedence. Some brandings can only be set
     * by one provider (eg. the main "about" logo) so the branding provider with the highest priority will
     * be the one which gets control of that feature.
     */
    @Override
    public int getPriority() {
        return 0;
    }

    /**
     * Get the primary branding colour for this API, the branding provider should return 0 if it
     * does not wish to override the branding colour. The branding colour is used for the mod list
     * entries and hyper-links within the about GUI panels, the colour returned should be fully opaque.
     */
    @Override
    public int getBrandingColour() {
        return ColorUtils.getARBG(255, 255, 255, 0);
        //return 0;
    }

    /**
     * Get the resource to use for the main logo, the API with the highest priority gets to define the
     * logo, this method can return null if this API does not want to override the logo
     */
    @Override
    public ResourceLocation getLogoResource() {
        return null;
    }

    /**
     * Gets the coordinates of the logo as an IIcon instance, only called if getLogoResource() returns
     * a non-null value and the logo will only be used if BOTH methods return a valid object.
     */
    @Override
    public IIcon getLogoCoords() {
        return null;
    }

    /**
     * Get the resource to use for the icon logo (the chicken in the default setup), the API with the
     * highest priority gets to define the icon logo, this method can return null if this API does not
     * want to override the icon
     */
    @Override
    public ResourceLocation getIconResource() {
        return null;
    }

    /**
     * Gets the coordinates of the icon logo as an IIcon instance, only called if getIconResource()
     * returns a non-null value and the icon will only be used if BOTH methods return a valid object.
     */
    @Override
    public IIcon getIconCoords() {
        return null;
    }

    /**
     * Get the display name for this API, used on the "about" screen, must not return null
     */
    @Override
    public String getDisplayName() {
        return Version.BL_VERSION.getFriendlyName();
    }

    /**
     * Get the copyright text for this API, used on the "about" screen, must not return null
     */
    @Override
    public String getCopyrightText() {
        return "Copyright (c) acomputerdog 2013-2014";
    }

    /**
     * Get the main home page URL for this API, used on the "about" screen, must not return null
     */
    @Override
    public URI getHomepage() {
        try {
            return new URL("http://www.blazeloader.com").toURI();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating BlazeLoader.com URI!", e);
        }
    }

    /**
     * If you wish to display a clickable twitter icon next to the API information in the "about" panel
     * then you must return values from this method as well as getTwitterAvatarResource() and
     * getTwitterAvatarCoords(). Return the twitter user name here.
     */
    @Override
    public String getTwitterUserName() {
        return "acomputerdog";
    }

    /**
     * If you wish to display a clickable twitter icon next to the API information, return the icon
     * resource here.
     */
    @Override
    public ResourceLocation getTwitterAvatarResource() {
        init();
        return twitterLoc;
    }

    /**
     * If you wish to display a clickable twitter icon next to the API information, return the icon
     * coordinates here.
     */
    @Override
    public IIcon getTwitterAvatarCoords() {
        init();
        return twitterIcon;
    }

}
