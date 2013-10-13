package net.acomputerdog.BlazeLoader.main.commands.bl.module;

import net.acomputerdog.BlazeLoader.main.Version;
import net.minecraft.src.ICommandSender;

public class ModuleVersion extends CommandBLModule {
    /**
     * Gets the number of arguments required.  For a varied amount return 0 and process manually.
     *
     * @return Return the number of required args.
     */
    @Override
    public int getNumRequiredArgs() {
        return 0;
    }

    /**
     * Gets the name of the module.
     *
     * @return Return the name of the module.
     */
    @Override
    public String getModuleName() {
        return "version";
    }

    /**
     * Gets the usage of the module.
     *
     * @return Return the usage of the module.
     */
    @Override
    public String getUsage() {
        return "version";
    }

    /**
     * Checks if the user has the required permissions to use the command.
     *
     * @param user The user attempting to perform the commands.
     * @return Return true if the user can use the command, false if not.
     */
    @Override
    public boolean canUserUseCommand(ICommandSender user) {
        return true;
    }

    /**
     * Executes the command.
     *
     * @param user The user executing the command.
     */
    @Override
    public void execute(ICommandSender user, String[] args) {
        sendChat(user, "BlazeLoader version [" + Version.getStringVersion() + "] on minecraft version [" + Version.getMinecraftVersion() + "].");
    }

    /**
     * Gets a concise description of the module's function.
     *
     * @return Return a concise description of the module's function.
     */
    @Override
    public String getModuleDescription() {
        return "Gets the version of BL.";
    }
}
