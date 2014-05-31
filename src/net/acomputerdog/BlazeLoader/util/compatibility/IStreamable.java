package net.acomputerdog.BlazeLoader.util.compatibility;

import java.io.InputStream;
import java.io.OutputStream;

public interface IStreamable {
    public OutputStream getWriteStream(String channel);
    public InputStream getReadStream(String channel);
}
