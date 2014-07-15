package net.acomputerdog.BlazeLoader.main;

import com.mumfrey.liteloader.api.*;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.acomputerdog.BlazeLoader.transformers.BLAccessTransformer;
import net.acomputerdog.BlazeLoader.version.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BlazeLoaderAPI implements LiteAPI {

    /**
     * Initialise this API, the API should do as little processing as possible here, but should also cache
     * the supplied environment and properties instances for later use
     *
     * @param environment
     * @param properties
     */
    @Override
    public void init(LoaderEnvironment environment, LoaderProperties properties) {
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
        BLAccessTransformer.AT_SOURCE_OVERRIDE = this.getClass().getResourceAsStream("/res/bl_at.cfg".replaceAll("/", File.separator));
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
        return Arrays.asList((CoreProvider)BlazeLoaderCP.instance);
    }

    /**
     * Should return a list of InterfaceProviders for this API, can return null if the API doesn't have any InterfaceProviders, (almost) guaranteed to only be called once
     */
    @Override
    public List<InterfaceProvider> getInterfaceProviders() {
        return Arrays.asList((InterfaceProvider)BlazeLoaderIP.instance);
    }

    /**
     * Should return a list of Observers for this API, can return null if the API doesn't have any Observers, (almost) guaranteed to only be called once
     */
    @Override
    public List<Observer> getObservers() {
        return null;
    }

    /**
     * Get the customisation providers for this API, can return null
     */
    @Override
    public List<CustomisationProvider> getCustomisationProviders() {
        return Arrays.asList((CustomisationProvider)BlazeLoaderBP.instance);
    }
}
