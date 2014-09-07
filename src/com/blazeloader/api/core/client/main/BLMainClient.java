package com.blazeloader.api.core.client.main;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.server.main.BLMainServer;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

public class BLMainClient extends BLMain {
    public BLMainClient(LoaderEnvironment environment, LoaderProperties properties) {
        super(environment, properties);
    }

    @Override
    public void init() {

    }

    @Override
    public boolean supportsServer() {
        return false;
    }

    @Override
    public boolean supportsClient() {
        return true;
    }

    @Override
    public BLMainClient getClient() {
        return this;
    }

    @Override
    public BLMainServer getServer() {
        throw new UnsupportedOperationException("This BLMain does not support BLMainServe!");
    }
}
