package net.acomputerdog.BlazeLoader.tweaklauncher;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.acomputerdog.BlazeLoader.proxy.MinecraftProxy;
import net.minecraft.src.MainProxyAuthenticator;
import net.minecraft.src.MainShutdownHook;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Session;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

/**
 * A modified, deobfuscated version of Main.class designed to load BlazeLoader.  Most code is copyright Mojang AB.
 */
public class BLMain {
    public static void main(String[] args)
    {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parser.accepts("demo");
        parser.accepts("fullscreen");
        ArgumentAcceptingOptionSpec optionServerIp = parser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec optionServerPort = parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        ArgumentAcceptingOptionSpec optionMainDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        ArgumentAcceptingOptionSpec optionAssetsDir = parser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionResPackDir = parser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionProxyHost = parser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec optionProxyPort = parser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionProxyUser = parser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec optionProxyPass = parser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec optionPlayerUsername = parser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        ArgumentAcceptingOptionSpec optionSessionId = parser.accepts("session").withRequiredArg();
        ArgumentAcceptingOptionSpec optionGameVersion = parser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec optionWindowWidth = parser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        ArgumentAcceptingOptionSpec optionWindowHeight = parser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        NonOptionArgumentSpec invalidOptionsParser = parser.nonOptions();
        OptionSet options = parser.parse(args);
        List invalidOptions = options.valuesOf(invalidOptionsParser);
        String proxyHost = (String)options.valueOf(optionProxyHost);
        Proxy proxy = Proxy.NO_PROXY;

        if (proxyHost != null) {
            try{
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, (Integer) options.valueOf(optionProxyPort)));
            }catch (Exception ignored){}
        }

        String proxyUser = (String)options.valueOf(optionProxyUser);
        String proxyPass = (String)options.valueOf(optionProxyPass);

        if (!proxy.equals(Proxy.NO_PROXY) && isStringEmpty(proxyUser) && isStringEmpty(proxyPass))
        {
            Authenticator.setDefault(new MainProxyAuthenticator(proxyUser, proxyPass));
        }

        int windowWidth = (Integer) options.valueOf(optionWindowWidth);
        int windowHeight = (Integer) options.valueOf(optionWindowHeight);
        boolean isFullscreen = options.has("fullscreen");
        boolean isDemo = options.has("demo");
        String gameVersion = (String)options.valueOf(optionGameVersion);
        File mainDir = (File)options.valueOf(optionMainDir);
        File assetDir = options.has(optionAssetsDir) ? (File)options.valueOf(optionAssetsDir) : new File(mainDir, "assets/");
        File resPackDir = options.has(optionResPackDir) ? (File)options.valueOf(optionResPackDir) : new File(mainDir, "resourcepacks/");
        Session session = new Session((String)optionPlayerUsername.value(options), (String)optionSessionId.value(options));
        MinecraftProxy theMinecraft = new MinecraftProxy(session, windowWidth, windowHeight, isFullscreen, isDemo, mainDir, assetDir, resPackDir, proxy, gameVersion);
        String serverIp = (String)options.valueOf(optionServerIp);

        if (serverIp != null)
        {
            theMinecraft.setServer(serverIp, (Integer) options.valueOf(optionServerPort));
        }

        Runtime.getRuntime().addShutdownHook(new MainShutdownHook());

        if (!invalidOptions.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + invalidOptions);
        }

        Thread.currentThread().setName("Minecraft main thread");
        theMinecraft.run();
    }

    private static boolean isStringEmpty(String str)
    {
        return str != null && !str.isEmpty();
    }
}
