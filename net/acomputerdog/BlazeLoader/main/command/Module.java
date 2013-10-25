package net.acomputerdog.BlazeLoader.main.command;

import net.acomputerdog.BlazeLoader.api.chat.ApiChat;
import net.acomputerdog.BlazeLoader.api.chat.EChatColor;
import net.minecraft.src.ICommandSender;

/**
 * Base class for modules of CommandBL
 */
public abstract class Module {

    /**
     * Gets the number of arguments required.  For a varied amount return 0 and process manually.
     * @return Return the number of required args.
     */
    public abstract int getNumRequiredArgs();

    /**
     * Gets the name of the module.
     * @return Return the name of the module.
     */
    public abstract String getModuleName();

    /**
     * Gets the usage of the module.
     * @return Return the usage of the module.
     */
    public abstract String getUsage();

    /**
     * Checks if the user has the required permissions to use the command.
     * @param user The user attempting to perform the commands.
     * @return Return true if the user can use the command, false if not.
     */
    public abstract boolean canUserUseCommand(ICommandSender user);

    /**
     * Executes the command.
     * @param user The user executing the command.
     * @param args The arguments passed to the module.
     *             -WARNING: args[0] is always the name of the sub-command!  Skip it!-
     */
    public abstract void execute(ICommandSender user, String[] args);

    /**
     * Gets a concise description of the module's function.
     * @return Return a concise description of the module's function.
     */
    public abstract String getModuleDescription();

    /**
     * Checks if the command sender is allowed to use the command.
     * @param sender The command user.
     * @param permissionLevel The permission level required.
     * @return Return true if the user can use the command, false if not.
     */
    protected boolean doesSenderHaveRequiredPermissions(ICommandSender sender, int permissionLevel){
        return sender.canCommandSenderUseCommand(permissionLevel, "bl");
    }

    /**
     * Sends chat to a command user.
     * @param target The user to send the chat to.
     * @param message The message to send.
     */
    protected void sendChat(ICommandSender target, String message){
        ApiChat.sendChat(target, message);
    }

    /**
     * Sends chat to a command user, followed by a format_reset marker.
     * @param target The user to send the chat to.
     * @param message The message to send.
     */
    protected void sendChatLine(ICommandSender target, String message){
        sendChat(target, message + EChatColor.FORMAT_RESET);
    }
    /**
     * Returns the name of the module.
     * @return Return CommandBLModule.getModuleName();
     */
    @Override
    public String toString() {
        return this.getModuleName();
    }
}
