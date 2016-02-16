package com.megahardcore.module.temporaryblock;


import com.megahardcore.MegaHardCore;
import com.megahardcore.service.ListenerModule;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;

public class TemporaryBlockHandler extends ListenerModule
{
    private final Map<LiteLocation, TemporaryBlock> temporaryBlockList = new HashMap<>();


    public TemporaryBlockHandler (MegaHardCore plugin)
    {
        super(plugin);
    }


    /**
     * int addTemporaryBlock(Block block)
     * removeBlock (int)
     * onBlockBreak -> mark as broken
     * onTempBlockBreakEvent
     * onZombieRespawnTask -> check if broken
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        if (temporaryBlockList.containsKey(LiteLocation.fromLocation(block.getLocation())))
        {
            TemporaryBlock temporaryBlock = temporaryBlockList.remove(LiteLocation.fromLocation(block.getLocation()));
            temporaryBlock.isBroken = true;
            plugin.getServer().getPluginManager().callEvent(new TemporaryBlockBreakEvent(temporaryBlock, event));
        }
    }


    public TemporaryBlock addTemporaryBlock(Location loc, Object... data)
    {
        TemporaryBlock temporaryBlock = new TemporaryBlock(loc, data);
        temporaryBlockList.put(LiteLocation.fromLocation(loc), temporaryBlock);
        return temporaryBlock;
    }
}
