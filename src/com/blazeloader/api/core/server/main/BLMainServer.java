package com.blazeloader.api.core.server.main;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.client.main.BLMainClient;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

public class BLMainServer extends BLMain {
    public BLMainServer(LoaderEnvironment environment, LoaderProperties properties) {
        super(environment, properties);
    }

    @Override
    public void init() {

    }

    @Override
    public boolean supportsServer() {
        return true;
    }

    @Override
    public boolean supportsClient() {
        return false;
    }

    @Override
    public BLMainClient getClient() {
        throw new UnsupportedOperationException("This BLMain does not support BLMainClient!");
    }

    @Override
    public BLMainServer getServer() {
        return this;
    }
}
