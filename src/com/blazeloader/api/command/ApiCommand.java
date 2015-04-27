package com.blazeloader.api.command;

import java.util.Collections;
import java.util.Map;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import com.blazeloader.bl.main.BLMain;

/**
 * Api functions related to commands
 */
public class ApiCommand {
    /**
     * Gets the command manager, if it exists. Otherwise returns a temporary handler that can be used.
     * <br>
     * If the real handler is created then all commands registered in the temporary are dumped into the actual handler.
     *
     * @return Return the command manager, or a temporary replacement.
     */
    public static CommandHandler getCommandManager() {
        return BLMain.instance().getCommandHandler();
    }

    /**
     * Registers a command, overriding it if it already exists.
     *
     * @param command The command to register.
     */
    public static void registerCommand(ICommand command) {
        getCommandManager().registerCommand(command);
    }
    
    /**
     * Gets a mapping of all commands.
     * @return an unmodifiable Map from command names to ICommand handlers
     */
    public static Map<String, ICommand> getCommands() {
    	return Collections.unmodifiableMap(getCommandManager().getCommands());
    }
    
    /**
     * Executes a command identified by a String.
     *
     * @param sender  The sender to report as having used the command.
     * @param command The command to execute.
     */
    public static void useCommand(ICommandSender sender, String command) {
        getCommandManager().executeCommand(sender, command);
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
