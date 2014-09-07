package com.blazeloader.api.direct.server.api.command;

import com.blazeloader.api.core.base.main.BLMain;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

/**
 * Api functions related to commands
 */
public class ApiCommand {

    /**
     * Gets the command manager, if it exists.  Otherwise returns a temporary handler that can be used.
     * If the real handler is created then all commands registered in the temporary are dumped into the actual handler.
     *
     * @return Return the command manager, or a temporary replacement.
     */
    public static CommandHandler getCommandManager() {
        return BLMain.commandHandler;
    }

    /**
     * Registers a command, overriding it if it already exists.
     *
     * @param command The command to register.
     */
    public static void registerCommand(ICommand command) {
        BLMain.commandHandler.registerCommand(command);
    }

    /**
     * Executes a command identified by a String.
     *
     * @param sender  The sender to report as having used the command.
     * @param command The command to execute.
     */
    public static void useCommand(ICommandSender sender, String command) {
        BLMain.commandHandler.executeCommand(sender, command);
    }

    /**
     * Executes a command identified by a command.
     *
     * @param sender  The sender to report as having used the command.
     * @param command The command to execute.
     */
    public static void useCommand(ICommandSender sender, ICommand command) {
        useCommand(sender, command.getCommandName());
    }
}
