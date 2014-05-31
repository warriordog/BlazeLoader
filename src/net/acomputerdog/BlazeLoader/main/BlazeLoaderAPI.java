package net.acomputerdog.BlazeLoader.main;

import com.mumfrey.liteloader.api.*;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.acomputerdog.BlazeLoader.event.BlazeLoaderIP;
import net.acomputerdog.BlazeLoader.transformers.BLAccessTransformer;
import net.acomputerdog.BlazeLoader.version.*;

import java.util.ArrayList;
import java.util.List;

public class BlazeLoaderAPI implements LiteAPI {
    private List<InterfaceProvider> interfaceProviders = new ArrayList<InterfaceProvider>();
    private List<CoreProvider> coreProviders = new ArrayList<CoreProvider>();

    /**
     * Initialise this API, the API should do as little processing as possible here, but should also cache
     * the supplied environment and properties instances for later use
     *
     * @param environment
     * @param properties
     */
    @Override
    public void init(LoaderEnvironment environment, LoaderProperties properties) {
        interfaceProviders.add(BlazeLoaderIP.instance);
        coreProviders.add(BlazeLoaderCP.instance);
        BLMain.init(environment, properties);
    }

    /**
     * Get the identifier for this API, the identifier is used to retrieve the API and match it against specified mod API dependencies
     */
    @Override
    public String getIdentifier() {
        return "BlazeLoader";
    }

    /**
     * Get the friendly name of this API
     */
    @Override
    public String getName() {
        return "BlazeLoader";
    }

    /**
     * Get the human-readable version of the API, can be any value
     */
    @Override
    public String getVersion() {
        return Version.getBLMainVersion().getVersionString();
    }

    /**
     * Get the revision number of this API. Unlike the version number, the revision number should only change when an incompatible
     * change is made to the APIs interfaces, it is also used when a mod specifies an API dependency using the api@revision syntax
     */
    @Override
    public int getRevision() {
        return Version.getBLMainVersion().getVersion2();
    }

    /**
     * Should return an array of required transformer names, these transformers will be injected UPSTREAM. Can return null.
     */
    @Override
    public String[] getRequiredTransformers() {
        BLAccessTransformer.AT_SOURCE_OVERRIDE = this.getClass().getResourceAsStream("bl_at.cfg");
        return new String[]{"net.acomputerdog.BlazeLoader.transformers.BLAccessTransformer"};
    }

    /**
     * Should return an array of required transformer names, these transformers will be injected DOWNSTREAM. Can return null.
     */
    @Override
    public String[] getRequiredDownstreamTransformers() {
        return null;
    }

    /**
     * Should return an array of required packet transformer names, these transformers will be injected UPSTREAM. Can return null.
     */
    @Override
    public String[] getPacketTransformers() {
        return null;
    }

    /**
     * Return a mod class prefix supported by this API, can return null if an API just wants to use "LiteMod" as a standard class name prefix
     */
    @Override
    public String getModClassPrefix() {
        return "BlazeMod";
    }

    /**
     * Should return a list of Enumerator modules to be injected, can return null if the API doesn't want to inject any additonal modules
     */
    @Override
    public List<EnumeratorModule> getEnumeratorModules() {
        return null;
    }

    /**
     * Should return a list of CoreProviders for this API, can return null if the API doesn't have any CoreProviders, (almost) guaranteed to only be called once
     */
    @Override
    public List<CoreProvider> getCoreProviders() {
        return coreProviders;
    }

    /**
     * Should return a list of InterfaceProviders for this API, can return null if the API doesn't have any InterfaceProviders, (almost) guaranteed to only be called once
     */
    @Override
    public List<InterfaceProvider> getInterfaceProviders() {

        return interfaceProviders;
    }

    /**
     * Should return a list of Observers for this API, can return null if the API doesn't have any Observers, (almost) guaranteed to only be called once
     */
    @Override
    public List<Observer> getObservers() {
        return null;
    }

    /**
     * Get the branding provider for this API, can return null
     */
    @Override
    public BrandingProvider getBrandingProvider() {
        return BlazeLoaderBP.instance;
    }
}
