package com.megahardcore.compatibility;


import com.megahardcore.MegaHardCore;
import com.megahardcore.service.MHCModule;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles compatibility for all supported plugins in one class
 *
 * @author Diemex
 */
public class CompatHandler extends MHCModule
{
    private static Set<IBlockProtection> blockProtectionPls;

    private static Set<IBlockLogger> blockLoggerPls;

    private static Set<IMonsterProtection> monsterProtectionPls;


    public CompatHandler (MegaHardCore plugin)
    {
        super(plugin);
    }


    public static boolean isExplosionProtected(Location loc)
    {
        if (loc != null)
            for (IBlockProtection prot : blockProtectionPls)
                if (prot.isExplosionProtected(loc))
                    return true;
        return false;
    }


    public static boolean canMonsterSpawn(Location loc)
    {
        if (loc != null)
            for (IMonsterProtection prot : monsterProtectionPls)
                if (prot.denySpawn(loc))
                    return true;
        return false;
    }


    public static void logFallingBlockFall(Block block)
    {
        if (block != null)
            for (IBlockLogger logger : blockLoggerPls)
                logger.logFallingBlockFall(block);
    }


    public static void logFallingBlockLand(BlockState block)
    {
        if (block != null)
            for (IBlockLogger logger : blockLoggerPls)
                logger.logFallingBlockLand(block);
    }


    @Override
    public void starting()
    {
        blockProtectionPls = new HashSet<>();
        monsterProtectionPls = new HashSet<>();
        blockLoggerPls = new HashSet<>();

        //BlockLoggers//
        //LogBlock
        CompatLogBlock compatLogBlock = new CompatLogBlock(plugin);
        if (compatLogBlock.isEnabled())
            blockLoggerPls.add(compatLogBlock);
    }


    @Override
    public void closing()
    {
        blockProtectionPls = null;
    }
}
