package net.acomputerdog.BlazeLoader.api.chat;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * API for chat-related functions.
 */
public class ApiChat {

    /**
     * Sends a raw chat to a command user.
     *
     * @param user    The command user to send the chat to.
     * @param message The message to send.
     */
    public static void sendChat(ICommandSender user, String message) {
        user.func_145747_a(new ChatComponentText(message));
    }
}
