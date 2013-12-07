package net.acomputerdog.BlazeLoader.api.command;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumChatFormatting;

/**
 * A modified version of ServerCommandManager.  Exactly the same but does not auto-load vanilla commands.
 */
public class BLCommandManager extends CommandHandler {

    /**
     * Sends a message to the admins of the server from a given CommandSender with the given resource string and given
     * extra strings. If the int par2 is even or zero, the original sender is also notified.
     */
    public void notifyAdmins(ICommandSender par1ICommandSender, int par2, String par3Str, Object ... par4ArrayOfObj)
    {
        boolean var5 = true;

        if (par1ICommandSender instanceof TileEntityCommandBlock && !MinecraftServer.getServer().worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput"))
        {
            var5 = false;
        }

        ChatMessageComponent var6 = ChatMessageComponent.createFromTranslationWithSubstitutions("chat.type.admin", par1ICommandSender.getCommandSenderName(), ChatMessageComponent.createFromTranslationWithSubstitutions(par3Str, par4ArrayOfObj));
        var6.setColor(EnumChatFormatting.GRAY);
        var6.setItalic(true);

        if (var5)
        {

            for (Object aPlayerEntityList : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                EntityPlayerMP var8 = (EntityPlayerMP) aPlayerEntityList;

                if (var8 != par1ICommandSender && MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(var8.getCommandSenderName())) {
                    var8.func_145747_a(var6);
                }
            }
        }

        if (par1ICommandSender != MinecraftServer.getServer())
        {
            MinecraftServer.getServer().func_145747_a(var6);
        }

        if ((par2 & 1) != 1)
        {
            par1ICommandSender.func_145747_a(ChatMessageComponent.createFromTranslationWithSubstitutions(par3Str, par4ArrayOfObj));
        }
    }
}
