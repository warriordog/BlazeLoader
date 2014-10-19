package com.blazeloader.api.main;

import com.blazeloader.api.version.Versions;
import com.mumfrey.liteloader.api.*;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

import java.util.List;

/**
 * BlazeLoader LiteAPI
 */
public class BlazeLoaderAPI implements LiteAPI {

    /**
     * Initialise this API, the API should do as little processing as possible here, but should also cache
     * the supplied environment and properties instances for later use
     *
     * @param environment LiteLoader-provided LoaderEnvironment
     * @param properties  LitLoader-provided LoaderProperties
     */
    @Override
    public void init(LoaderEnvironment environment, LoaderProperties properties) {
        if (environment.getType() == LoaderEnvironment.EnvironmentType.CLIENT) {
            new BLMainClient(environment, properties).init();
        } else {
            new BLMain(environment, properties).init();
        }
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
        return Versions.getBLMainVersion().getVersionString();
    }

    /**
     * Get the revision number of this API. Unlike the version number, the revision number should only change when an incompatible
     * change is made to the APIs interfaces, it is also used when a mod specifies an API dependency using the api@revision syntax
     */
    @Override
    public int getRevision() {
        return Versions.getBLMainVersion().getVersion2();
    }

    /**
     * Should return an array of required transformer names, these transformers will be injected UPSTREAM. Can return null.
     */
    @Override
    public String[] getRequiredTransformers() {
        return BLMain.instance().getRequiredTransformers();
    }

    /**
     * Should return an array of required transformer names, these transformers will be injected DOWNSTREAM. Can return null.
     */
    @Override
    public String[] getRequiredDownstreamTransformers() {
        return BLMain.instance().getRequiredDownstreamTransformers();
    }

    /**
     * Return a mod class prefix supported by this API, can return null if an API just wants to use "LiteMod" as a standard class name prefix
     */
    @Override
    public String getModClassPrefix() {
        return "BlazeMod";
    }

    /**
     * Should return a list of Enumerator modules to be injected, can return null if the API doesn't want to inject any additional modules
     */
    @Override
    public List<EnumeratorModule> getEnumeratorModules() {
        return BLMain.instance().getEnumeratorModules();
    }

    /**
     * Should return a list of CoreProviders for this API, can return null if the API doesn't have any CoreProviders, (almost) guaranteed to only be called once
     */
    @Override
    public List<CoreProvider> getCoreProviders() {
        return BLMain.instance().getCoreProviders();
    }

    /**
     * Should return a list of InterfaceProviders for this API, can return null if the API doesn't have any InterfaceProviders, (almost) guaranteed to only be called once
     */
    @Override
    public List<InterfaceProvider> getInterfaceProviders() {
        return BLMain.instance().getInterfaceProviders();
    }

    @Override
    public List<Observer> getPreInitObservers() {
        return BLMain.instance().getPreInitObservers();
    }

    /**
     * Should return a list of Observers for this API, can return null if the API doesn't have any Observers, (almost) guaranteed to only be called once
     */
    @Override
    public List<Observer> getObservers() {
        return BLMain.instance().getObservers();
    }

    /**
     * Get the customisation providers for this API, can return null
     */
    @Override
    public List<CustomisationProvider> getCustomisationProviders() {
        return BLMain.instance().getCustomisationProviders();
    }
}
