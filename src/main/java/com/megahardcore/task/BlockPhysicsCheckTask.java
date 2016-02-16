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

package com.megahardcore.task;


import com.megahardcore.MegaHardCore;
import com.megahardcore.config.RootConfig;
import com.megahardcore.config.RootNode;
import com.megahardcore.module.BlockModule;
import com.megahardcore.service.config.customtypes.BlockTypeList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Called to apply physics to a block and its neighbors if necessary.
 */
public class BlockPhysicsCheckTask implements Runnable
{
    /**
     * Plugin instance.
     */
    private final MegaHardCore plugin;

    /**
     * Target block.
     */
    private final Block block;

    /**
     * Recursion count.
     */
    private final int recursionCount;

    /**
     * Will the the adjacent blocks be checked no matter if the center falls or not?
     */
    private final boolean force;

    /**
     * Config Reference
     */
    private final RootConfig CFG;


    /**
     * Constructor.
     *
     * @param plugin         - Plugin instance.
     * @param block          - Target block for task.
     * @param recursionCount - Recursion count for task.
     * @param force          - do we want to check adjacent blocks no matter if the center block falls or not? Also checks a lot
     *                       further down
     */
    public BlockPhysicsCheckTask (MegaHardCore plugin, Block block, int recursionCount, boolean force)
    {
        this.plugin = plugin;
        this.block = block;
        this.recursionCount = recursionCount;
        this.force = force;
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    @Override
    public void run()
    {

        BlockModule module = plugin.getModuleForClass(BlockModule.class);
        boolean fall = false;

        final boolean fallingBlocksEnabled = CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_ENABLE, block.getWorld().getName());
        final BlockTypeList fallingBlocks = CFG.getBlocktypeList(RootNode.MORE_FALLING_BLOCKS, block.getWorld().getName());

        Material material = block.getType();
        Block underBlock = block.getRelative(BlockFace.DOWN);

        if ((underBlock.getType() == Material.AIR || underBlock.isLiquid() || underBlock.getType() == Material.TORCH)
                && (material == Material.SAND || material == Material.GRAVEL || fallingBlocks.contains(block)
                && fallingBlocksEnabled && material != Material.AIR))
        {
            module.applyPhysics(block, true);
            fall = true;
        }

        if (fall || force)
        {
            if (recursionCount >= 0)
            {
                if (force)
                {
                    Block neighbor = block.getRelative(BlockFace.DOWN, 1);
                    module.physicsCheck(neighbor, recursionCount - 1, false, 1);

                    neighbor = block.getRelative(BlockFace.DOWN, 2);
                    module.physicsCheck(neighbor, recursionCount - 1, false, 2);

                    neighbor = block.getRelative(BlockFace.DOWN, 3);
                    module.physicsCheck(neighbor, recursionCount - 1, false, 3);

                    neighbor = block.getRelative(BlockFace.DOWN, 4);
                    module.physicsCheck(neighbor, recursionCount - 1, false, 4);

                    neighbor = block.getRelative(BlockFace.DOWN, 5);
                    module.physicsCheck(neighbor, recursionCount - 1, false, 5);

                    neighbor = block.getRelative(BlockFace.DOWN, 6);
                    module.physicsCheck(neighbor, recursionCount - 1, false, 6);
                }

                Block neighbor = block.getRelative(BlockFace.UP);
                module.physicsCheck(neighbor, recursionCount - 1, false, 1);

                neighbor = block.getRelative(BlockFace.DOWN);
                module.physicsCheck(neighbor, recursionCount - 1, false, 2);

                neighbor = block.getRelative(BlockFace.EAST);
                module.physicsCheck(neighbor, recursionCount - 1, false, 3);

                neighbor = block.getRelative(BlockFace.WEST);
                module.physicsCheck(neighbor, recursionCount - 1, false, 4);

                neighbor = block.getRelative(BlockFace.NORTH);
                module.physicsCheck(neighbor, recursionCount - 1, false, 5);

                neighbor = block.getRelative(BlockFace.SOUTH);
                module.physicsCheck(neighbor, recursionCount - 1, false, 6);
            }
        }
    }
}
