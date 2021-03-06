package com.megahardcore.task;


import com.megahardcore.MegaHardCore;
import com.megahardcore.config.RootConfig;
import com.megahardcore.config.RootNode;
import com.megahardcore.config.messages.MsgCategory;
import com.megahardcore.module.MsgModule;
import com.megahardcore.module.PlayerModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WeightCheckTask implements Runnable
{
    private final MegaHardCore mPlugin;
    private final RootConfig   CFG;
    private final MsgModule    mMessenger;
    private static final HashMap<UUID, Long> mLastClicks = new HashMap<>();
    private static final Lock                lock        = new ReentrantLock();


    public WeightCheckTask (MegaHardCore plugin)
    {
        this.mPlugin = plugin;
        CFG = plugin.getModuleForClass(RootConfig.class);
        mMessenger = plugin.getModuleForClass(MsgModule.class);
    }


    @Override
    public void run()
    {
        if (lock.tryLock()) //Prevent concurrent modification
        {
            try
            {
                for (UUID playerUuuid : mLastClicks.keySet())
                {
                    Player player = mPlugin.getServer().getPlayer(playerUuuid);
                    //Remove players that haven't clicked in their inventory for 5 seconds
                    if (System.currentTimeMillis() - mLastClicks.get(playerUuuid) > 5000 || player == null)
                    {
                        mLastClicks.remove(playerUuuid);
                        mMessenger.hidePopup(player, MsgCategory.WEIGHT_MSG.getUniqueIdentifier());
                    } else
                    {
                        final double armorPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_ARMOR_POINTS, player.getWorld().getName());
                        final double invPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_INV_POINTS, player.getWorld().getName());
                        final double toolPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_TOOL_POINTS, player.getWorld().getName());
                        final double maxPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_MAX_POINTS, player.getWorld().getName());

                        final float weight = PlayerModule.inventoryWeight(player, (float) armorPoints, (float) invPoints, (float) toolPoints);

                        List<String> weightMessage = new ArrayList<>(2);
                        weightMessage.add(String.format("Peso: %.1f/%.1f", weight, maxPoints));
                        weightMessage.add(weight > maxPoints ? ChatColor.RED + "Demasiado Peso" : ChatColor.GREEN + "Sin Problemas");
                        mMessenger.sendPopup(player, MsgCategory.WEIGHT_MSG, weightMessage, false);
                    }
                }
            } finally
            {
                lock.unlock();
            }
        }
    }


    public static void updateLastCLick(UUID uuid)
    {
        mLastClicks.put(uuid, System.currentTimeMillis());
    }
}
