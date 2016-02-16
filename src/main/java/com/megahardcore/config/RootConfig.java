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

package com.megahardcore.config;


import com.megahardcore.MegaHardCore;
import com.megahardcore.service.config.ConfigNode;
import com.megahardcore.service.config.Header;
import com.megahardcore.service.config.MultiWorldConfig;
import com.megahardcore.service.config.YamlCommentWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class RootConfig extends MultiWorldConfig
{
    public static MHCConfig mainMHCConfig = null;

    /**
     * Constructor
     */
    public RootConfig (MegaHardCore plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        load();
    }


    @Override
    public void closing()
    {
    }


    @Override
    public void load()
    {
        init();
        //find all ymls
        File[] configFiles = findAllYmlFiles(plugin.getDataFolder());
        //load the ymls
        MHCConfig[] mhcConfigs = new MHCConfig[configFiles.length];
        for (int i = 0; i < configFiles.length; i++)
        {
            mhcConfigs[i] = new MHCConfig(configFiles[i]);
            mhcConfigs[i].registerNodes(RootNode.values());
            mhcConfigs[i].load();
        }
        //what is the main config.yml file?
        for (MHCConfig mhcConfig : mhcConfigs)
        {
            if (mhcConfig.isMainConfig())
            {
                mainMHCConfig = mhcConfig;
                break;
            }
        }
        //has config.yml been found? not -> create it
        if (mainMHCConfig == null)
        {
            File mainFile = new File(plugin.getDataFolder().getPath() + File.separator + "config.yml");
            if (!mainFile.exists())
            {
                try
                {
                    mainFile.getParentFile().mkdirs();
                    mainFile.createNewFile();
                } catch (IOException e)
                {
                    plugin.getLogger().severe("Couldn't create config.yml");
                    e.printStackTrace();
                }
            }
            mainMHCConfig = new MHCConfig(mainFile);
            mainMHCConfig.registerNodes(RootNode.values());
            mainMHCConfig.load();
        }
        //Load config.yml
        enabledForAll = mainMHCConfig.isEnabledForAll();
        for (Map.Entry<ConfigNode, Object> node : mainMHCConfig.getLoadedNodes().entrySet())
        {
            for (String world : mainMHCConfig.getWorlds())
            {
                set(world, node.getKey(), node.getValue());
            }
        }
        //Save files
        mainMHCConfig.setHeader(createHeader());
        mainMHCConfig.save();

        //Prepare comments
        Map<String, String[]> comments = new HashMap<>();
        for (RootNode node : RootNode.values())
            if (node.getComments() != null)
                comments.put(node.getPath(), node.getComments());

        if (mainMHCConfig.printComments()) YamlCommentWriter.write(mainMHCConfig.getConfigFile(), comments);
    }


    private Header createHeader()
    {
        Header header = new Header();
        header.setHeading("MegaHardCore Config");
        String[] lines = new String[]{
                "",
                "1. The config cleans itself, so if something resets you probably did something wrong",
                "2. Generally if you can specify a block you can add meta after an @",
                "   F.e: STEP@3 = cobblestone slab. STEP@3,11 matches normal&upside cobble slabs",
                "   If you specify meta it will only match cobble slabs and not the other slabs.",
                "   If you don't specify meta it matches all slabs.",
                "   You can use numerical block ids as well, they will be converted to bukkit names",
                "3. If your empty lists reset, put [] instead",
                "4. This config changes regularly, so you might want to revisit it after an update.",
                "5. Lots of the configuration is user requested so if you need something just ask", "6. Remember to use /mhc reload after you changed the config instead of /reload",
                "",
                "Happy Configuring!"};
        header.addLines(lines);
        return header;
    }


    /**
     * find all yml files
     * loop over all files
     * determine main file
     * if doesn't exist -> create
     * load main file
     * loop remaining files <-
     * merge configs
     * determine disable/inherit values
     * save files with correct inheritance
     */


    /**
     * Search the base directory for yml-files
     *
     * @return File[] containing all the *.yml Files in a lexical order
     */
    protected File[] findAllYmlFiles(File baseDir)
    {
        String[] filePaths = baseDir.list((dir, name) -> {
            return name.endsWith(".yml"); //TODO - disables
        });
        if (filePaths == null) filePaths = new String[]{};
        Arrays.sort(filePaths); //lexically
        ArrayList<File> files = new ArrayList<>();
        for (String fileName : filePaths)
            files.add(new File(plugin.getDataFolder() + File.separator + fileName));
        return files.toArray(new File[files.size()]);
    }
}