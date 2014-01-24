package net.BlazeLoader.main.command;

import net.acomputerdog.BlazeLoader.api.chat.EChatColor;
import net.acomputerdog.BlazeLoader.mod.Mod;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.command.ICommandSender;

import java.util.List;

/**
 * Module that adds /bl mods.  Gives list of loaded mods to the player.
 */
public class ModuleMods extends Module {

    @Override
    public int getNumRequiredArgs() {
        return 0;
    }

    @Override
    public String getModuleName() {
        return "mods";
    }

    @Override
    public String getUsage() {
        return "mods";
    }

    @Override
    public boolean canUserUseCommand(ICommandSender user) {
        return true;
    }

    @Override
    public void execute(ICommandSender user, String[] args) {
        List<Mod> mods = ModList.getLoadedMods();
        if (mods.size() > 0) {
            sendChatLine(user, EChatColor.COLOR_AQUA + "" + EChatColor.FORMAT_UNDERLINE + mods.size() + " loaded mod(s):");
            sendChat(user, "");
            for (Mod mod : mods) {
                sendChatLine(user, EChatColor.COLOR_YELLOW + mod.getModName() + EChatColor.COLOR_WHITE + " v. " + mod.getStringModVersion() + EChatColor.COLOR_ORANGE + " - " + mod.getModDescription());
            }
        } else {
            sendChatLine(user, EChatColor.COLOR_RED + "No mods are loaded!");
        }
    }

    @Override
    public String getModuleDescription() {
        return "Displays the list of loaded mods.";
    }
}
