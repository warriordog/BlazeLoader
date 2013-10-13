package net.acomputerdog.BlazeLoader.main.commands.bl;

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
        if(true)throw new RuntimeException("Debug crash");
        return "bl";
    }

    @Override
    public String getCommandUsage(ICommandSender user) {
        return "/bl [function] [args]";
    }

    @Override
    public void processCommand(ICommandSender user, String[] args) {
        if(args.length == 0){
            sendChat(user, "Loaded BL sub-commands:");
            if(modules.size() > 0){
                for(int index = 0; index < modules.size(); index++){
                    CommandBLModule module = modules.get(index);
                    sendChat(user, index + ". " + module.getModuleName() + " - " + module.getModuleDescription());
                }
            }else{
                sendChat(user, "No sub-commands loaded!");
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
        for(CommandBLModule module : modules){
            if(module.getNumRequiredArgs() < command.length + 1){ // account for first index being module name
                if(module.canUserUseCommand(user)){
                    module.execute(user, command);
                }else{
                    sendChat(user, "You cannot use this command, sorry!");
                }
            }else{
                sendChat(user, "Invalid arguments!  Use \"/bl " + module.getUsage() + "\".");
            }
            break;
        }
        sendChat(user, "Unknown sub-command!  Use \"/bl\" to get a list!");
    }
}
