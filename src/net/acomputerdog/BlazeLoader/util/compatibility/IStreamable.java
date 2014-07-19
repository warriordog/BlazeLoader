package net.acomputerdog.BlazeLoader.util.compatibility;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Allows an object to communicate with other arbitrary objects through data streams.
 * The data does not have to stay within the JVM; it is permitted to read/write to them externally (such as from a file or over a network).
 * Because the data is sent through streams, there is no guarantee that this IStreamable will receive or send data at a specific time, or that it will at all.
 */
public interface IStreamable {

    /**
     * Gets an OutputStream that allows writing to the owning object on the specified channel.
     * @param channel The channel to bind to.
     * @return Returns an OutputStream that allows writing to the owning object.
     */
    public OutputStream getWriteStream(String channel);

    /**
     * Gets an InputStream that allows reading from the owning object on the specified channel.
     * @param channel The channel to bind to.
     * @return Returns an InputStream that allows reading from the owning object.
     */
    public InputStream getReadStream(String channel);

    /**
     * Checks if this IStreamable supports the specified channel.
     *
     * @param channel The channel name to check.
     * @return Return true if the channel is supported, false otherwise.
     */
    public boolean supportsChannel(String channel);
}
