/*
 * This file is part of
 * MegaHardCore Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * MegaHardCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaHardCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with MegaHardCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.megahardcore.features;


import com.megahardcore.MegaHardCore;
import com.megahardcore.config.RootConfig;
import com.megahardcore.config.RootNode;
import com.megahardcore.config.messages.MessageNode;
import com.megahardcore.module.MsgModule;
import com.megahardcore.module.PlayerModule;
import com.megahardcore.service.Feature;
import com.megahardcore.service.ListenerModule;
import com.megahardcore.service.PermissionNode;
import com.megahardcore.task.RemoveExposedTorchesTask;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.material.Torch;

/**
 * Torches
 * <p/>
 * can't be attached to loose blocks , get washed away when it rains
 */
public class Torches extends ListenerModule
{
    private RootConfig CFG;

    private MsgModule messenger;

    private PlayerModule playerModule;


    public Torches (MegaHardCore plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        messenger = plugin.getModuleForClass(MsgModule.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
    }


    /**
     * When a block is placed
     * <p/>
     * players can't attach torches to loose blocks like sand/dirt
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent placeEvent)
    {
        Player player = placeEvent.getPlayer();
        Block block = placeEvent.getBlock();
        World world = block.getWorld();

        final boolean limitedTorchPlacement = CFG.getBoolean(RootNode.LIMITED_TORCH_PLACEMENT, world.getName());
        final boolean soundFizzEnabled = CFG.getBoolean(RootNode.SOUNDS_TORCH_FIZZ, world.getName());
        final int torchMinY = CFG.getInt(RootNode.STANDARD_TORCH_MIN_Y, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.TORCHES);

        // FEATURE: players can't attach torches to common "soft" blocks
        if (block.getType().equals(Material.TORCH) && limitedTorchPlacement && !playerBypasses)
        {
            Torch torch = new Torch(Material.TORCH, block.getData());
            Material attachmentMaterial = block.getRelative(torch.getAttachedFace()).getType();

            if (attachmentMaterial == Material.DIRT || attachmentMaterial == Material.GRASS || attachmentMaterial == Material.LONG_GRASS
                    || attachmentMaterial == Material.SAND || attachmentMaterial == Material.GRAVEL)
            {
                if (soundFizzEnabled)
                    messenger.send(player, MessageNode.LIMITED_TORCH_PLACEMENTS, PermissionNode.SILENT_LIMITED_TORCH_PLACEMENT, Sound.FIZZ, 20);
                placeEvent.setCancelled(true);
            }
        }

        // FEATURE: no standard torches, jack o lanterns, or fire on top of netherrack near diamond level
        if (torchMinY > 0 && !playerBypasses)
        {
            if (world.getEnvironment() == World.Environment.NORMAL
                    && block.getY() < torchMinY
                    && (block.getType() == Material.TORCH || block.getType() == Material.JACK_O_LANTERN || (block.getType() == Material.FIRE && block
                    .getRelative(BlockFace.DOWN).getType() == Material.NETHERRACK)))
            {
                messenger.send(player, MessageNode.NO_TORCHES_HERE, PermissionNode.SILENT_NO_TORCHES_HERE, Sound.FIZZ, 20);
                placeEvent.setCancelled(true);
            }
        }
    }


    /**
     * When the weather changes... rainfall breaks exposed torches (exposed to the sky)
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        World world = event.getWorld();

        final boolean rainBreaksTorchesEnabled = CFG.getBoolean(RootNode.RAIN_BREAKS_TORCHES, world.getName());
        final boolean snowBreaksCrops = CFG.getBoolean(RootNode.SNOW_BREAKS_CROPS, world.getName());

        if (event.toWeatherState()) //is it raining
        {
            if (rainBreaksTorchesEnabled || snowBreaksCrops)
            {
                // plan to remove torches chunk by chunk gradually throughout the rainperiod
                Chunk[] chunks = world.getLoadedChunks();
                if (chunks.length > 0)
                {
                    int startOffset = plugin.getRandom().nextInt(chunks.length);
                    for (int i = 0; i < chunks.length; i++)
                    {
                        Chunk chunk = chunks[(startOffset + i) % chunks.length];

                        RemoveExposedTorchesTask task = new RemoveExposedTorchesTask(plugin, chunk);
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, i * 15L);
                    }
                }
            }
        }
    }
}
