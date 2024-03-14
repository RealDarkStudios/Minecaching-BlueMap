package net.realdarkstudios.minecachingbm.commands;

import de.bluecolored.bluemap.api.BlueMapAPI;
import net.realdarkstudios.minecachingbm.MinecachingBlueMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReloadCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("minecachingbm.reload")) BlueMapAPI.onEnable(MinecachingBlueMap.getInstance()::reloadMarkers);
        else commandSender.sendMessage("You do not have permission to run this command!");
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
