package com.blazeloader.api.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import com.blazeloader.api.chat.ApiChat;
import com.blazeloader.api.chat.ChatColor;

/**
 * A superclass for commands that wish to include BL utilities.
 */
public abstract class BLCommandBase extends CommandBase {

    /**
     * Creates a new BLCommandBase without automatically registering it.
     */
    public BLCommandBase() {
        this(false);
    }

    /**
     * Creates a new BLCommandBase, optionally auto-registering it in the command list.
     *
     * @param autoRegister If set to true, command will be automatically registered.
     */
    public BLCommandBase(boolean autoRegister) {
        super();
        if (autoRegister) {
            ApiCommand.registerCommand(this);
        }
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
    	if (getIgnorePermissionLevel()) {
    		//EntityPlayerMP is hard wired to return false for anyone that is not an OP for any command except for /help
    		//It's a terrible design
    		return sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), "help");
    	}
    	return super.canCommandSenderUseCommand(sender);
    }

    /**
     * Sends chat to a command user.
     *
     * @param target  The user to send the chat to.
     * @param message The message to send.
     */
    protected void sendChat(ICommandSender target, String message) {
        ApiChat.sendChat(target, message);
    }

    /**
     * Sends chat to a command sender, followed by a format_reset marker.
     *
     * @param target  The user to send the chat to.
     * @param message The message to send.
     */
    protected void sendChatLine(ICommandSender target, String message) {
        sendChat(target, ChatColor.FORMAT_RESET.format(message));
    }

    /**
     * Returns the name of this command.
     *
     * @return Returns BLCommandBase.getCommandName();
     */
    @Override
    public String toString() {
        return getCommandName();
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public abstract int getRequiredPermissionLevel();
    
    /**
     * Return whether this command is usable by non-opped users on a server, like /help
     */
    public abstract boolean getIgnorePermissionLevel();
}
