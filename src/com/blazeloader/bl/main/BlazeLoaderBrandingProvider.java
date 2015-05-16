package com.blazeloader.bl.main;

import com.blazeloader.api.client.render.ApiRenderClient;
import com.blazeloader.util.version.Versions;
import com.mumfrey.liteloader.api.BrandingProvider;
import com.mumfrey.liteloader.client.util.render.IconAbsolute;
import com.mumfrey.liteloader.util.render.Icon;
import net.minecraft.util.ResourceLocation;

import java.net.URI;
import java.net.URL;

/**
 * BlazeLoader BrandingProvider
 */
public class BlazeLoaderBrandingProvider implements BrandingProvider {
    public static final BlazeLoaderBrandingProvider instance = new BlazeLoaderBrandingProvider();
    
    private static final ResourceLocation twitterLoc = new ResourceLocation("blazeloader", "tex/twitter_logo.png");
    private static final Icon twitterIcon = new IconAbsolute(twitterLoc, "twitter", 32, 32, 0, 0, 32, 32, 32);
    
    private BlazeLoaderBrandingProvider() {
    }
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public int getBrandingColour() {
        return ApiRenderClient.getARGB(255, 255, 255, 0);
    }
    
    @Override
    public ResourceLocation getLogoResource() {
        return null;
    }
    
    @Override
    public Icon getLogoCoords() {
        return null;
    }
    
    @Override
    public ResourceLocation getIconResource() {
        return null;
    }
    
    @Override
    public Icon getIconCoords() {
        return null;
    }
    
    @Override
    public String getDisplayName() {
        return Versions.BL_VERSION.getFriendlyName();
    }
    
    @Override
    public String getCopyrightText() {
        return "Copyright (c) acomputerdog 2013-2015";
    }
    
    @Override
    public URI getHomepage() {
        try {
            return new URL("http://www.blazeloader.com").toURI();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating BlazeLoader.com URI!", e);
        }
    }
    
    @Override
    public String getTwitterUserName() {
        return "acomputerdog";
    }
    
    @Override
    public ResourceLocation getTwitterAvatarResource() {
        return twitterLoc;
    }
    
    @Override
    public Icon getTwitterAvatarCoords() {
        return twitterIcon;
    }
}
