package net.sacredlabyrinth.Phaed.PreciousStones.managers;

import java.util.logging.Level;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import me.taylorkelly.help.Help;

import java.util.HashSet;
import java.util.List;
import org.bukkit.World;

import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.sacredlabyrinth.Phaed.PreciousStones.Helper;
import net.sacredlabyrinth.Phaed.PreciousStones.TargetBlock;
import net.sacredlabyrinth.Phaed.PreciousStones.ChatBlock;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.*;
import net.sacredlabyrinth.Phaed.PreciousStones.FieldSettings;
import net.sacredlabyrinth.Phaed.PreciousStones.PlayerData;

/**
 *
 * @author phaed
 */
public final class CommandManager implements CommandExecutor
{
    private PreciousStones plugin;
    public Help helpPlugin;
    private ChatBlock cacheBlock = new ChatBlock();

    /**
     *
     * @param plugin
     */
    public CommandManager()
    {
        plugin = PreciousStones.getInstance();
        registerHelpCommands();
    }

    /**
     *
     * @return
     */
    public ChatBlock getCacheBlock()
    {
        cacheBlock = new ChatBlock();
        return cacheBlock;
    }

    /**
     *
     */
    public void registerHelpCommands()
    {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Help");

        if (helpPlugin == null)
        {
            if (test != null)
            {
                helpPlugin = ((Help) test);
            }
        }

        if (helpPlugin != null)
        {
            helpPlugin.registerCommand("ps on/off ", " Disable/Enable the placing of pstones", plugin, true, "preciousstones.benefit.onoff");
            helpPlugin.registerCommand("ps allow [player/g:group/t:team/*] ", "To overlapped fields", plugin, true, "preciousstones.whitelist.allow");
            helpPlugin.registerCommand("ps allowall [player/g:group/t:team/*] ", "To all your fields", plugin, true, "preciousstones.whitelist.allowall");
            helpPlugin.registerCommand("ps remove [player/g:group/t:team/*] ", "From overlapped fields", plugin, true, "preciousstones.whitelist.remove");
            helpPlugin.registerCommand("ps removeall [player/g:group/t:team/*] ", "From all your fields", plugin, true, "preciousstones.whitelist.removeall");
            helpPlugin.registerCommand("ps allowed ", "List allowed players in overlapped fields", plugin, true, "preciousstones.whitelist.allowed");
            helpPlugin.registerCommand("ps who ", "List all inhabitants inside the overlapping fields", plugin, true, "preciousstones.whitelist.who");
            helpPlugin.registerCommand("ps setname [name] ", "Set the name of fields", plugin, true, "preciousstones.benefit.setname");
            helpPlugin.registerCommand("ps setradius [radius]", "Sets the field's radius", plugin, true, "preciousstones.benefit.setradius");
            helpPlugin.registerCommand("ps setheight [height]", "Sets the field's height", plugin, true, "preciousstones.benefit.setheight");
            helpPlugin.registerCommand("ps setvelocity [.1-5] ", "Sets velocity of launchers/cannons", plugin, true, "preciousstones.benefit.setvelocity");
            helpPlugin.registerCommand("ps setowner [player] ", "Of the block you're pointing at", plugin, true, "preciousstones.admin.setowner");
            helpPlugin.registerCommand("ps visualize ", "Visualizes the perimiter of the field", plugin, true, "preciousstones.benefit.visualize");
            helpPlugin.registerCommand("ps mark ", "Marks the location of fields", plugin, true, "preciousstones.benefit.mark");
            helpPlugin.registerCommand("ps snitch <clear> ", "View/clear snitch you're pointing at", plugin, true, "preciousstones.benefit.snitch");
            helpPlugin.registerCommand("ps delete ", "Delete the field(s) you're standing on", plugin, true, "preciousstones.admin.delete");
            helpPlugin.registerCommand("ps delete [player] ", "Delete all pstones of the player", plugin, true, "preciousstones.admin.delete");
            helpPlugin.registerCommand("ps info ", "Get info for the field youre standing on", plugin, true, "preciousstones.admin.info");
            helpPlugin.registerCommand("ps counts ", "View your field counts", plugin, true, "preciousstones.benefit.counts");
            helpPlugin.registerCommand("ps counts [typeid/player] ", "View player field counts list", plugin, true, "preciousstones.admin.counts");
            helpPlugin.registerCommand("ps list [chunks-in-radius]", "Lists all pstones in area", plugin, true, "preciousstones.admin.list");
            helpPlugin.registerCommand("ps reload ", "Reload configuraton file", plugin, true, "preciousstones.admin.reload");
            helpPlugin.registerCommand("ps fields ", "List the configured field types", plugin, true, "preciousstones.admin.fields");
            helpPlugin.registerCommand("ps clean ", "Cleans up all orphan fields in the world", plugin, true, "preciousstones.admin.clean");
            helpPlugin.registerCommand("ps clean ", "Reverts all orphan fields in the world", plugin, true, "preciousstones.admin.revert");

            PreciousStones.log(Level.INFO, "Help plugin support enabled");
        }
    }

    /**
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        try
        {
            if (sender instanceof Player)
            {
                if (command.getName().equals("ps"))
                {
                    Player player = (Player) sender;

                    if (plugin.getSettingsManager().isBlacklistedWorld(player.getWorld()))
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "PreciousStones disabled in this world");
                        return true;
                    }

                    if (args.length > 0)
                    {
                        String cmd = args[0];
                        args = Helper.removeFirst(args);

                        Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

                        if (cmd.equals("debug") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.debug"))
                        {
                            plugin.getSettingsManager().setDebug(!plugin.getSettingsManager().isDebug());
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Debug output " + (plugin.getSettingsManager().isDebug() ? "enabled" : "disabled"));
                            return true;
                        }
                        if (cmd.equals("debugdb") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.debug"))
                        {
                            plugin.getSettingsManager().setDebugdb(!plugin.getSettingsManager().isDebugdb());
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Debug db output " + (plugin.getSettingsManager().isDebugdb() ? "enabled" : "disabled"));
                            return true;
                        }
                        if (cmd.equals("debugsql") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.debug"))
                        {
                            plugin.getSettingsManager().setDebugsql(!plugin.getSettingsManager().isDebugsql());
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Debug sql output " + (plugin.getSettingsManager().isDebugsql() ? "enabled" : "disabled"));
                            return true;
                        }
                        if (cmd.equals("on") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.onoff"))
                        {
                            if (plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled())
                            {
                                plugin.getPlayerManager().getPlayerData(player.getName()).setDisabled(false);
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "Enabled the placing of pstones");
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Pstone placement is already enabled");
                            }
                            return true;
                        }
                        else if (cmd.equals("off") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.onoff"))
                        {
                            if (!plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled())
                            {
                                plugin.getPlayerManager().getPlayerData(player.getName()).setDisabled(true);
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "Disabled the placing of pstones");
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Pstone placement is already disabled");
                            }
                            return true;
                        }
                        else if (cmd.equals("allow") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.allow"))
                        {
                            if (args.length >= 1)
                            {
                                Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                if (field != null)
                                {
                                    for (String playerName : args)
                                    {
                                        int count = plugin.getForceFieldManager().addAllowed(player, field, playerName);

                                        if (count > 0)
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(playerName) + " has been allowed in " + count + Helper.plural(count, " field", "s"));
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(playerName) + " is already on the list");
                                        }
                                    }
                                }
                                else
                                {
                                    plugin.getCommunicationManager().showNotFound(player);
                                }

                                return true;
                            }
                        }
                        else if (cmd.equals("remove") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.remove"))
                        {
                            if (args.length >= 1)
                            {
                                Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                if (field != null)
                                {
                                    for (String playerName : args)
                                    {
                                        int count = plugin.getForceFieldManager().removeAllowed(player, field, playerName);

                                        if (count > 0)
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(playerName) + " was removed from " + count + Helper.plural(count, " field", "s"));
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + Helper.capitalize(playerName) + " not found or is the last player on the list");
                                        }
                                    }
                                }
                                else
                                {
                                    plugin.getCommunicationManager().showNotFound(player);
                                }

                                return true;
                            }
                        }
                        else if (cmd.equals("allowall") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.allowall"))
                        {
                            if (args.length >= 1)
                            {
                                for (String playerName : args)
                                {
                                    int count = plugin.getForceFieldManager().allowAll(player, playerName);

                                    if (count > 0)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(playerName) + " has been allowed in " + count + Helper.plural(count, " field", "s"));
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(playerName) + " is already on all your lists");
                                    }
                                }

                                return true;
                            }
                        }
                        else if (cmd.equals("removeall") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.removeall"))
                        {
                            if (args.length >= 1)
                            {
                                for (String playerName : args)
                                {
                                    int count = plugin.getForceFieldManager().removeAll(player, playerName);

                                    if (count > 0)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(playerName) + " was removed " + count + Helper.plural(count, " field", "s"));
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(playerName) + " is not in any of your lists");
                                    }
                                }

                                return true;
                            }
                        }
                        else if (cmd.equals("allowed") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.allowed"))
                        {
                            Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                            if (field != null)
                            {
                                HashSet<String> allowed = plugin.getForceFieldManager().getAllowed(player, field);

                                if (allowed.size() > 0)
                                {
                                    String out = "";

                                    for (String ae : allowed)
                                    {
                                        out += ", " + ae;
                                    }

                                    ChatBlock.sendMessage(player, ChatColor.YELLOW + "Allowed: " + ChatColor.AQUA + out.substring(2));
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "No players allowed in this field");
                                }
                            }
                            else
                            {
                                plugin.getCommunicationManager().showNotFound(player);
                            }

                            return true;
                        }
                        else if (cmd.equals("who") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.who"))
                        {
                            Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                            if (field != null)
                            {
                                HashSet<String> inhabitants = plugin.getForceFieldManager().getWho(player, field);

                                if (inhabitants.size() > 0)
                                {
                                    String out = "";

                                    for (String i : inhabitants)
                                    {
                                        out += ", " + i;
                                    }

                                    ChatBlock.sendMessage(player, ChatColor.YELLOW + "Inhabitants: " + ChatColor.AQUA + out.substring(2));
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "No players found in these overlapped fields");
                                }
                            }
                            else
                            {
                                plugin.getCommunicationManager().showNotFound(player);
                            }

                            return true;
                        }
                        else if (cmd.equals("setname") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setname"))
                        {
                            if (args.length >= 1)
                            {
                                String playerName = Helper.toMessage(args);

                                if (playerName.length() > 0)
                                {
                                    Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                    if (field != null)
                                    {
                                        int count = plugin.getForceFieldManager().setNameFields(player, field, playerName);

                                        if (count > 0)
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Renamed " + count + Helper.plural(count, " field", "s") + " to " + playerName);
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "No nameable fields found");
                                        }
                                        return true;
                                    }
                                    else
                                    {
                                        plugin.getCommunicationManager().showNotFound(player);
                                    }
                                    return true;
                                }
                            }
                        }
                        else if (cmd.equals("setradius") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setradius"))
                        {
                            if (args.length == 1 && Helper.isInteger(args[0]))
                            {
                                int radius = Integer.parseInt(args[0]);

                                Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                if (field != null)
                                {
                                    FieldSettings fs = field.getSettings();

                                    if (radius >= 0 && radius <= fs.getRadius())
                                    {
                                        field.setRadius(radius);
                                        plugin.getStorageManager().offerField(field);
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Radius set to " + radius);
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Radius must be less than or equal to " + fs.getRadius());
                                    }
                                    return true;
                                }
                                else
                                {
                                    plugin.getCommunicationManager().showNotFound(player);
                                }
                                return true;
                            }
                        }
                        else if (cmd.equals("setheight") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setheight"))
                        {
                            if (args.length == 1 && Helper.isInteger(args[0]))
                            {
                                int height = Integer.parseInt(args[0]);

                                Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                if (field != null)
                                {
                                    FieldSettings fs = field.getSettings();

                                    int maxHeight = (((fs.getRadius() * 2) + 1) + fs.getHeight());

                                    if (height >= 0 && height <= maxHeight)
                                    {
                                        field.setHeight(height);
                                        plugin.getStorageManager().offerField(field);
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Height set to " + height);
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Height must be less than or equal to " + maxHeight);
                                    }
                                    return true;
                                }
                                else
                                {
                                    plugin.getCommunicationManager().showNotFound(player);
                                }
                                return true;
                            }
                        }
                        else if (cmd.equals("setvelocity") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setvelocity"))
                        {
                            if (args.length == 1 && Helper.isFloat(args[0]))
                            {
                                float velocity = Float.parseFloat(args[0]);

                                Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                if (field != null)
                                {
                                    FieldSettings fs = field.getSettings();

                                    if (fs.hasVeocityFlag())
                                    {
                                        if (velocity < 0 || velocity > 5)
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "Velocity must be from 0 to 5");
                                            return true;
                                        }

                                        field.setVelocity(velocity);
                                        plugin.getStorageManager().offerField(field);
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Velocity set to " + velocity);
                                    }
                                    else
                                    {
                                        plugin.getCommunicationManager().showNotFound(player);
                                    }
                                    return true;
                                }
                                else
                                {
                                    plugin.getCommunicationManager().showNotFound(player);
                                }
                                return true;
                            }
                        }
                        else if (cmd.equals("visualize") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.visualize"))
                        {
                            if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.visualize"))
                            {
                                plugin.getVisualizationManager().revertVisualization(player);

                                List<Field> fieldsInArea = plugin.getForceFieldManager().getFieldsInCustomArea(player.getLocation(), plugin.getSettingsManager().getVisualizeAdminChunkRadius());

                                if (fieldsInArea.size() > 0)
                                {
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Generating visualization...");

                                    for (Field f : fieldsInArea)
                                    {
                                        plugin.getVisualizationManager().addVisualizationField(player, f);
                                    }

                                    plugin.getVisualizationManager().displayVisualization(player, true);
                                }
                            }
                            else
                            {
                                plugin.getVisualizationManager().revertVisualization(player);

                                Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                if (field != null)
                                {
                                    HashSet<Field> fields = plugin.getForceFieldManager().getOverlappedFields(player, field);

                                    if (fields != null)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Generating visualization...");

                                        for (Field f : fields)
                                        {
                                            plugin.getVisualizationManager().addVisualizationField(player, f);
                                        }

                                        plugin.getVisualizationManager().displayVisualization(player, true);
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "You are not inside of a field");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not inside of a field");
                                }
                            }
                            return true;
                        }
                        else if (cmd.equals("mark") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.mark"))
                        {
                            if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.mark"))
                            {
                                List<Field> fieldsInArea = plugin.getForceFieldManager().getFieldsInCustomArea(player.getLocation(), plugin.getSettingsManager().getVisualizeMarkChunkRadius());

                                if (fieldsInArea.size() > 0)
                                {
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Marking  " + fieldsInArea.size() + " field blocks...");

                                    for (Field f : fieldsInArea)
                                    {
                                        plugin.getVisualizationManager().addFieldMark(player, f);
                                    }

                                    plugin.getVisualizationManager().displayVisualization(player, false);
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "No fields in the area");
                                }
                            }
                            else
                            {
                                List<Field> fieldsInArea = plugin.getForceFieldManager().getFieldsInCustomArea(player.getLocation(), plugin.getSettingsManager().getVisualizeMarkChunkRadius());

                                if (fieldsInArea.size() > 0)
                                {
                                    int count = 0;
                                    for (Field f : fieldsInArea)
                                    {
                                        if (plugin.getForceFieldManager().isAllowed(f, player.getName()))
                                        {
                                            count++;
                                            plugin.getVisualizationManager().addFieldMark(player, f);
                                        }
                                    }

                                    if (count > 0)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Marking  " + count + " field blocks...");
                                        plugin.getVisualizationManager().displayVisualization(player, false);
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "No fields in the area");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "No fields in the area");
                                }
                            }

                            return true;
                        }
                        else if (cmd.equals("snitch") && !plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.snitch"))
                        {
                            if (args.length == 0)
                            {
                                Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                if (field != null)
                                {
                                    plugin.getCommunicationManager().showSnitchList(player, field);
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not pointing at a snitch block");
                                }

                                return true;
                            }
                            else if (args.length == 1)
                            {
                                if (args[0].equals("clear"))
                                {
                                    Field field = plugin.getForceFieldManager().getOneAllowedField(block, player);

                                    if (field != null)
                                    {
                                        boolean cleaned = plugin.getForceFieldManager().cleanSnitchList(player, field);

                                        if (cleaned)
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Cleared the snitch list");
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "Snitch list is empty");
                                        }
                                    }
                                    else
                                    {
                                        plugin.getCommunicationManager().showNotFound(player);
                                    }
                                    return true;
                                }
                            }
                        }
                        else if (cmd.equals("more") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.snitch"))
                        {
                            if (cacheBlock.size() > 0)
                            {
                                ChatBlock.sendBlank(player);

                                cacheBlock.sendBlock(player, plugin.getSettingsManager().getLinesPerPage());

                                if (cacheBlock.size() > 0)
                                {
                                    ChatBlock.sendBlank(player);
                                    ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + "Type /ps more to view next page.");
                                }
                                ChatBlock.sendBlank(player);

                                return true;
                            }

                            ChatBlock.sendMessage(player, ChatColor.GOLD + "Nothing more to see.");
                            return true;
                        }
                        else if (cmd.equals("counts"))
                        {
                            if (args.length == 0 && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.counts"))
                            {
                                if (!plugin.getCommunicationManager().showPlayerCounts(player, player.getName()))
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "Player does not have any fields");
                                }
                                return true;
                            }

                            if (args.length == 1 && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.counts"))
                            {
                                if (Helper.isInteger(args[0]))
                                {
                                    int type = Integer.parseInt(args[0]);

                                    if (!plugin.getCommunicationManager().showCounts(player, type))
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Not a valid field type");
                                    }
                                }
                                else if (Helper.isString(args[0]))
                                {
                                    String playerName = args[0].toString();

                                    if (!plugin.getCommunicationManager().showPlayerCounts(player, playerName))
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Player does not have any fields");
                                    }
                                }
                                return true;
                            }
                            return false;
                        }
                        else if (cmd.equals("info") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.info"))
                        {
                            Field pointing = plugin.getForceFieldManager().getOneAllowedField(block, player);
                            List<Field> fields = plugin.getForceFieldManager().getSourceFields(block.getLocation());

                            if (pointing != null && !fields.contains(pointing))
                            {
                                fields.add(pointing);
                            }

                            plugin.getCommunicationManager().showFieldDetails(player, fields);

                            if (fields.isEmpty())
                            {
                                plugin.getCommunicationManager().showNotFound(player);
                            }
                            return true;
                        }
                        else if (cmd.equals("delete") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.delete"))
                        {
                            if (args.length == 0)
                            {
                                List<Field> sourcefields = plugin.getForceFieldManager().getSourceFields(block.getLocation());

                                if (sourcefields.size() > 0)
                                {
                                    int count = plugin.getForceFieldManager().deleteFields(player, sourcefields.get(0));

                                    if (count > 0)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Protective field removed from " + count + Helper.plural(count, " field", "s"));

                                        if (plugin.getSettingsManager().isLogBypassDelete())
                                        {
                                            PreciousStones.log(Level.INFO, "Protective field removed from {0}{1} by {2} near {3}", count, Helper.plural(count, " field", "s"), player.getName(), sourcefields.get(0).toString());
                                        }
                                    }
                                    else
                                    {
                                        plugin.getCommunicationManager().showNotFound(player);
                                    }
                                }
                                else
                                {
                                    plugin.getCommunicationManager().showNotFound(player);
                                }

                                return true;
                            }
                            else if (args.length == 1)
                            {
                                Player badplayer = Helper.matchSinglePlayer(args[1]);

                                if (badplayer != null)
                                {
                                    int fields = plugin.getForceFieldManager().deleteBelonging(badplayer.getName());
                                    int ubs = plugin.getForceFieldManager().deleteBelonging(badplayer.getName());

                                    if (fields > 0)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Deleted " + badplayer.getName() + "'s " + fields + " fields");
                                    }

                                    if (ubs > 0)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Deleted " + badplayer.getName() + "'s " + fields + " unbreakables");
                                    }

                                    if (ubs == 0 && fields == 0)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "The player had no pstones");
                                    }
                                }
                                else
                                {
                                    plugin.getCommunicationManager().showNotFound(player);
                                }
                                return true;
                            }
                        }
                        else if (cmd.equals("setowner") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.setowner"))
                        {
                            if (args.length == 1)
                            {
                                String owner = args[0];

                                if (owner.contains(":"))
                                {
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Cannot assign groups as owners");
                                    return true;
                                }

                                TargetBlock tb = new TargetBlock(player, 100, 0.2, plugin.getSettingsManager().getThroughFields());

                                if (tb != null)
                                {
                                    Block targetblock = tb.getTargetBlock();

                                    if (targetblock != null)
                                    {
                                        Field field = plugin.getForceFieldManager().getField(targetblock);

                                        if (field != null)
                                        {
                                            // transfer the count over to the new owner

                                            PlayerData oldData = plugin.getPlayerManager().getPlayerData(field.getOwner());
                                            oldData.decrementFieldCount(field.getTypeId());

                                            PlayerData newData = plugin.getPlayerManager().getPlayerData(owner);
                                            newData.incrementFieldCount(field.getTypeId());

                                            // change the owner

                                            field.setOwner(owner);
                                            plugin.getStorageManager().offerField(field);
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Owner set to " + owner);
                                            return true;
                                        }
                                    }
                                }

                                ChatBlock.sendMessage(player, ChatColor.AQUA + "You are not pointing at a field or unbreakable block");
                                return true;
                            }
                        }
                        else if (cmd.equals("list") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.list"))
                        {
                            if (args.length == 1)
                            {
                                if (Helper.isInteger(args[0]))
                                {
                                    int chunk_radius = Integer.parseInt(args[0]);

                                    List<Unbreakable> unbreakables = plugin.getUnbreakableManager().getUnbreakablesInArea(player, chunk_radius);
                                    List<Field> fields = plugin.getForceFieldManager().getFieldsInCustomArea(player.getLocation(), chunk_radius);

                                    for (Unbreakable u : unbreakables)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + u.toString());
                                    }

                                    for (Field f : fields)
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + f.toString());
                                    }

                                    if (unbreakables.isEmpty() && fields.isEmpty())
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "No field or unbreakable blocks found");
                                    }
                                    return true;
                                }
                            }
                        }
                        else if (cmd.equals("reload") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.reload"))
                        {
                            plugin.getSettingsManager().load();
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Configuration reloaded");
                            return true;
                        }
                        else if (cmd.equals("fields") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.fields"))
                        {
                            plugin.getCommunicationManager().showConfiguredFields(player);
                            return true;
                        }
                        else if (cmd.equals("clean") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.clean"))
                        {
                            List<World> worlds = plugin.getServer().getWorlds();

                            int cleandFF = 0;
                            int cleandU = 0;

                            for (World world : worlds)
                            {
                                cleandFF += plugin.getForceFieldManager().cleanOrphans(world);
                                cleandU += plugin.getUnbreakableManager().cleanOrphans(world);
                            }

                            if (cleandFF > 0)
                            {
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "Cleaned " + cleandFF + " orphaned fields");
                            }
                            if (cleandU > 0)
                            {
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "Cleaned " + cleandFF + " orphaned unbreakable blocks");
                            }
                            if (cleandFF == 0 && cleandU == 0)
                            {
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "No orphans found");
                            }
                            return true;
                        }
                        else if (cmd.equals("revert") && plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.revert"))
                        {
                            List<World> worlds = plugin.getServer().getWorlds();

                            int cleandFF = 0;
                            int cleandU = 0;

                            for (World world : worlds)
                            {
                                cleandFF = +plugin.getForceFieldManager().revertOrphans(world);
                                cleandU = +plugin.getUnbreakableManager().revertOrphans(world);
                            }

                            if (cleandFF > 0)
                            {
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "Reverted " + cleandFF + " orphaned fields");
                            }
                            if (cleandU > 0)
                            {
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "Reverted " + cleandFF + " orphaned unbreakable blocks");
                            }

                            if (cleandFF == 0 && cleandU == 0)
                            {
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "No orphan fields/unbreakables found");
                            }
                            return true;
                        }

                        ChatBlock.sendMessage(player, ChatColor.RED + "Not a valid command or insufficient permissions");

                        return true;
                    }

                    ChatColor color = plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() ? ChatColor.DARK_GRAY : ChatColor.YELLOW;
                    String status = plugin.getPlayerManager().getPlayerData(player.getName()).isDisabled() ? ChatColor.GRAY + " - disabled" : "";

                    cacheBlock = new ChatBlock();

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.onoff"))
                    {
                        cacheBlock.addRow(ChatColor.YELLOW + "  /ps on/off " + ChatColor.AQUA + "- Disable/Enable the placing of pstones");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.allow"))
                    {
                        cacheBlock.addRow(color + "  /ps allow [player/g:group/t:team/*] " + ChatColor.AQUA + "- To overlapped fields");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.allowall"))
                    {
                        cacheBlock.addRow(color + "  /ps allowall [player/g:group/t:team/*] " + ChatColor.AQUA + "- To all your fields");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.remove"))
                    {
                        cacheBlock.addRow(color + "  /ps remove [player/g:group/t:team/*] " + ChatColor.AQUA + "- From overlapped fields");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.removeall"))
                    {
                        cacheBlock.addRow(color + "  /ps removeall [player/g:group/t:team/*] " + ChatColor.AQUA + "- From all your fields");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.whitelist.allowed"))
                    {
                        cacheBlock.addRow(color + "  /ps allowed " + ChatColor.AQUA + "- List all allowed players in overlapped fields");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.who"))
                    {
                        cacheBlock.addRow(color + "  /ps who " + ChatColor.AQUA + "- List all inhabitants inside the overlapped fields");
                    }

                    if (plugin.getSettingsManager().haveLimits() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.counts"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps counts " + ChatColor.AQUA + "- View your field counts");
                    }

                    if (plugin.getSettingsManager().haveNameable() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setname"))
                    {
                        cacheBlock.addRow(color + "  /ps setname [name] " + ChatColor.AQUA + "- Set the name of fields");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setradius"))
                    {
                        cacheBlock.addRow(color + "  /ps setradius [radius] " + ChatColor.AQUA + "- Sets the field's radius");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setheight"))
                    {
                        cacheBlock.addRow(color + "  /ps setheight [height] " + ChatColor.AQUA + "- Sets the field's height");
                    }

                    if (plugin.getSettingsManager().haveVelocity() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.setvelocity"))
                    {
                        cacheBlock.addRow(color + "  /ps setvelocity [.1-5] " + ChatColor.AQUA + "- For launchers/cannons (0=auto)");
                    }

                    if (plugin.getSettingsManager().haveSnitch() && plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.snitch"))
                    {
                        cacheBlock.addRow(color + "  /ps snitch <clear> " + ChatColor.AQUA + "- View/clear snitch you're pointing at");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.visualize") || plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.visualize"))
                    {
                        cacheBlock.addRow(color + "  /ps visualize" + ChatColor.AQUA + "- Visualizes the perimiter of the field");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.benefit.mark") || plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.mark"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps mark" + ChatColor.AQUA + "- Marks the location of fields");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.delete"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps delete " + ChatColor.AQUA + "- Delete the field(s) you're standing on");
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps delete [player] " + ChatColor.AQUA + "- Delete all pstones of the player");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.info"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps info " + ChatColor.AQUA + "- Get info for the field youre standing on");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.counts"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps counts [typeid/player] " + ChatColor.AQUA + "- View player field counts list");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.list"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps list [chunks-in-radius]" + ChatColor.AQUA + "- Lists all pstones in area");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.setowner"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps setowner [player] " + ChatColor.AQUA + "- Of the block you're pointing at");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.reload"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps reload " + ChatColor.AQUA + "- Reloads configuraton file");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.fields"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps fields " + ChatColor.AQUA + "- List the configured field types");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.clean"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps clean " + ChatColor.AQUA + "- Cleans up all orphan fields in the world");
                    }

                    if (plugin.getPermissionsManager().hasPermission(player, "preciousstones.admin.revert"))
                    {
                        cacheBlock.addRow(ChatColor.DARK_RED + "  /ps revert " + ChatColor.AQUA + "- Reverts all orphan fields in the world");
                    }

                    if (cacheBlock.size() > 0)
                    {
                        ChatBlock.sendBlank(player);
                        ChatBlock.saySingle(player, ChatColor.WHITE + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion() + status + ChatColor.DARK_GRAY + " ----------------------------------------------------------------------------------------");
                        ChatBlock.sendBlank(player);

                        boolean more = cacheBlock.sendBlock(player, plugin.getSettingsManager().getLinesPerPage());

                        if (more)
                        {
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + "Type /ps more to view next page.");
                        }

                        ChatBlock.sendBlank(player);
                    }
                    return true;
                }
            }
        }
        catch (Exception ex)
        {
            PreciousStones.log(Level.SEVERE, "Command entry error: {0}", ex.getMessage());
        }

        return false;
    }
}
