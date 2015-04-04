package com.blazeloader.api.compatibility;

/**
 * Allows an object to communicate with other arbitrary objects by passing String data.
 * <br>The data does not have to stay within the JVM; it is permitted to read/write to them externally (such as from a file or over a network).
 * Because the data is passed directly through methods, it is safe to assume that the data passed will be used immediately and by the calling thread.
 */
public interface IWritable {

    /**
     * Passes the given data to this Writable on the specified channel.
     *
     * @param channel The channel to bind to.
     * @param data    The data to pass.
     */
    public void writeData(String channel, String data);

    /**
     * Reads any available data from this Writable on the specified channel.
     *
     * @param channel The channel to bind to.
     * @return Return any available data or null if none is available.
     */
    public String readData(String channel);

    /**
     * Checks if this Writable supports the specified channel.
     *
     * @param channel The channel name to check.
     * @return Return true if the channel is supported, false otherwise.
     */
    public boolean supportsChannel(String channel);
}
