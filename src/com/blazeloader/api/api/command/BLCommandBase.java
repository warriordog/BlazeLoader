package com.blazeloader.api.api.command;

import com.blazeloader.api.api.chat.ApiChat;
import com.blazeloader.api.api.chat.EChatColor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

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
        sendChat(target, message + EChatColor.FORMAT_RESET);
    }

    /**
     * Returns the name of this command.
     *
     * @return Returns BLCommandBase.getCommandName();
     */
    @Override
    public String toString() {
        return this.getCommandName();
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public abstract int getRequiredPermissionLevel();
}
