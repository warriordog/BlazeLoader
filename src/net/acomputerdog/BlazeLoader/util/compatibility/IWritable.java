package net.acomputerdog.BlazeLoader.util.compatibility;

public interface IWritable {
    public void writeData(String channel, String data);
    public String readData(String channel);
}
