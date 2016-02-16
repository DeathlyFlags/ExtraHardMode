package com.megahardcore.task;


import com.megahardcore.MegaHardCore;
import com.megahardcore.config.RootConfig;
import com.megahardcore.config.RootNode;
import com.megahardcore.module.MsgModule;
import com.megahardcore.module.PlayerModule;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class ArmorWeightTask implements Runnable
{
    private final MegaHardCore mPlugin;
    private final RootConfig   CFG;
    private final MsgModule    mMessenger;
    private static Set<UUID> mPlayerList = new HashSet<>();


    public ArmorWeightTask (MegaHardCore plugin)
    {
        mPlugin = plugin;
        CFG = plugin.getModuleForClass(RootConfig.class);
        mMessenger = plugin.getModuleForClass(MsgModule.class);
    }


    @Override
    public void run()
    {
        for (Player player : mPlugin.getServer().getOnlinePlayers())
        {
            if (!CFG.getBoolean(RootNode.ARMOR_SLOWDOWN_ENABLE, player.getWorld().getName()))
                continue;
            final float baseSpeed = (float) CFG.getDouble(RootNode.ARMOR_SLOWDOWN_BASESPEED, player.getWorld().getName());
            final int slowdownPercent = CFG.getInt(RootNode.ARMOR_SLOWDOWN_PERCENT, player.getWorld().getName());
            final float armorPoints = PlayerModule.getArmorPoints(player);
            if (armorPoints != 0 && !player.isFlying() && player.getGameMode() != GameMode.CREATIVE)
            {
                float value = baseSpeed * (1 - armorPoints / 0.8F * (slowdownPercent / 100F));
                player.setWalkSpeed(value);
            } else
                player.setWalkSpeed(baseSpeed);
        }
    }
}
