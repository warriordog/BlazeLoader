package net.acomputerdog.BlazeLoader.main.commands.bl;

import net.acomputerdog.BlazeLoader.api.chat.EChatColor;
import net.acomputerdog.BlazeLoader.api.command.BLCommandBase;
import net.acomputerdog.BlazeLoader.main.commands.bl.module.CommandBLModule;
import net.acomputerdog.BlazeLoader.main.commands.bl.module.ModuleVersion;
import net.minecraft.src.ICommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A base command for BlazeLoader.
 */
public class CommandBL extends BLCommandBase {
    protected List<CommandBLModule> modules;

    public CommandBL(){
        super(true);
        modules = new ArrayList<CommandBLModule>();
        modules.add(new ModuleVersion());
    }

    @Override
    public String getCommandName() {
        return "bl";
    }

    @Override
    public String getCommandUsage(ICommandSender user) {
        return "/bl [function] [args]";
    }

    @Override
    public void processCommand(ICommandSender user, String[] args) {
        if(args.length == 0){
            sendChatLine(user, EChatColor.COLOR_AQUA + "" + EChatColor.FORMAT_UNDERLINE + "Loaded BL sub-commands:");
            if(modules.size() > 0){
                for(int index = 0; index < modules.size(); index++){
                    CommandBLModule module = modules.get(index);
                    sendChatLine(user, EChatColor.COLOR_BLUE + "" + index + ". "  + EChatColor.COLOR_YELLOW + module.getModuleName()  + " - " + EChatColor.COLOR_ORANGE + module.getModuleDescription());
                }
            }else{
                sendChatLine(user, EChatColor.COLOR_RED + "No sub-commands loaded!");
            }
        }else{
            processSubCommand(user, args);
        }
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("blazeloader");
    }

    protected void processSubCommand(ICommandSender user, String[] command){
        if(command.length == 0){
            throw new IllegalArgumentException("command must have at least one index!");
        }
        for(CommandBLModule module : modules){
            if(module.getModuleName().equals(command[0])){
                if(module.getNumRequiredArgs() < command.length + 1){ // account for first index being module name
                    if(module.canUserUseCommand(user)){
                        module.execute(user, command);
                    }else{
                        sendChatLine(user, EChatColor.COLOR_RED + "You cannot use this command, sorry!");
                    }
                }else{
                    sendChatLine(user, EChatColor.COLOR_RED + "Invalid arguments!  Use \"/bl " + module.getUsage() + "\".");
                }
                return;
            }
        }
        sendChatLine(user, EChatColor.COLOR_RED + "Unknown sub-command!  Use \"/bl\" to get a list!");
    }
}
