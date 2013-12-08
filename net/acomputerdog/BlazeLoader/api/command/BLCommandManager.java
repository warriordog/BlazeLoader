package net.acomputerdog.BlazeLoader.api.command;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

/**
 * A modified version of ServerCommandManager.  Exactly the same but does not auto-load vanilla commands.
 */
public class BLCommandManager extends CommandHandler {

    /**
     * Sends a message to the admins of the server from a given CommandSender with the given resource string and given
     * extra strings. If the int par2 is even or zero, the original sender is also notified.
     */
    public void notifyAdmins(ICommandSender par1ICommandSender, int par2, String par3Str, Object... par4ArrayOfObj) {
        boolean var5 = true;

        if (par1ICommandSender instanceof TileEntityCommandBlock && !MinecraftServer.getServer().worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput")) {
            var5 = false;
        }

        ChatComponentTranslation var6 = new ChatComponentTranslation("chat.type.admin", par1ICommandSender.getCommandSenderName(), new ChatComponentTranslation(par3Str, par4ArrayOfObj));
        var6.func_150255_a(new ChatStyle().func_150238_a(EnumChatFormatting.GRAY));
        var6.func_150255_a(new ChatStyle().func_150238_a(EnumChatFormatting.ITALIC));

        if (var5) {

            for (Object aPlayerEntityList : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                EntityPlayerMP var8 = (EntityPlayerMP) aPlayerEntityList;

                if (var8 != par1ICommandSender && MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(var8.getCommandSenderName())) {
                    var8.func_145747_a(var6);
                }
            }
        }

        if (par1ICommandSender != MinecraftServer.getServer()) {
            MinecraftServer.getServer().func_145747_a(var6);
        }

        if ((par2 & 1) != 1) {
            par1ICommandSender.func_145747_a(new ChatComponentTranslation(par3Str, par4ArrayOfObj));
        }
    }
}
