/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ryanhamshire.ExtraHardMode.module;

import me.ryanhamshire.ExtraHardMode.ExtraHardMode;
import me.ryanhamshire.ExtraHardMode.service.EHMModule;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Map;

/**
 * Put all the Utility Stuff here that doesn't fit into the other modules
 */
@SuppressWarnings("SameParameterValue")
public class UtilityModule extends EHMModule
{
    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public UtilityModule(ExtraHardMode plugin)
    {
        super(plugin);
    }

    /**
     * Generates a Firework with random colors/velocity and the given Firework Type
     *
     * @param type The type of firework
     * @return nothing
     */
    public void fireWorkRandomColors(FireworkEffect.Type type, Location location)
    {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        //Generate the colors
        int rdmInt1 = plugin.getRandom().nextInt(255);
        int rdmInt2 = plugin.getRandom().nextInt(255);
        int rdmInt3 = plugin.getRandom().nextInt(255);
        Color mainColor = Color.fromRGB(rdmInt1, rdmInt2, rdmInt3);

        FireworkEffect fwEffect = FireworkEffect.builder().withColor(mainColor).with(type).build();
        fireworkMeta.addEffect(fwEffect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }

    /**
     * Returns if Material is a plant that should be affected by the farming Rules
     */
    public boolean isPlant(Material material)
    {
        return material.equals(Material.CROPS)
                || material.equals(Material.POTATO)
                || material.equals(Material.CARROT)
                || material.equals(Material.MELON_STEM)
                || material.equals(Material.PUMPKIN_STEM);
    }

    /**
     * Verifies if we are in a CraftingGrid and not in a furnace or similar
     * @param event
     * @return
     */
    public boolean isCraftInv (CraftItemEvent event)
    {
        if (event.getInventory() != null && event.getSlotType().equals(InventoryType.SlotType.RESULT))
        {
            //Only care about workbenches and the built in crafting
            return (event.getInventory().getType().equals(InventoryType.WORKBENCH) ||
                    event.getInventory().getType().equals(InventoryType.CRAFTING));
        }
        return false;
    }

    /**
     * Counts the number of items of a specific type
     * @param inv to count in
     * @param toCount the Material to count
     * @return the number of items as Integer
     */
    public static int countInvItem (PlayerInventory inv, Material toCount)
    {
        int counter = 0;
        for (ItemStack stack : inv.getContents())
        {
            if (stack != null && stack.getType().equals(toCount))
            {
                counter += stack.getAmount();
            }
        }
        return counter;
    }

    public boolean isSameShape(ArrayList<ItemStack> recipe1, ArrayList<ItemStack> recipe2)
    {
        //compare recipes
        boolean isSame = true;
        for (int i = 0; i < recipe1.size(); i++)
        {
            if (!recipe1.get(i).equals(recipe2.get(i)))
                isSame = false;
        }
        return isSame;
    }

    public boolean isSameRecipe (ShapedRecipe recipe1, ShapedRecipe recipe2)
    {
        boolean isSameResult = recipe1.getResult().getAmount() == recipe2.getResult().getAmount()
                && recipe1.getResult().getType().equals(recipe2.getResult().getType());
        boolean isSameShape = isSameShape(recipeToArrayList(recipe1), recipeToArrayList(recipe2));
        return isSameShape && isSameResult;
    }

    private ArrayList<ItemStack> recipeToArrayList(ShapedRecipe recipe)
    {
        String [] shape = recipe.getShape();
        Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();

        //Create an ArrayList with the actual recipe based of the shape and ingredientMap
        ArrayList <ItemStack> craftRecipe = new ArrayList <ItemStack>();
        String flatShape = strArrToStr(shape);
        for (int i = 0; i < flatShape.length(); i++)
        {
            char id = flatShape.charAt(i);
            ItemStack itemStack = ingredientMap.get(id);
            //to avoid null pointers, empty slots will be filled with air
            if (itemStack == null) itemStack = new ItemStack(Material.AIR);
            craftRecipe.add(itemStack);
        }
        return craftRecipe;
    }

    public String strArrToStr (String [] arr)
    {
        StringBuilder builder = new StringBuilder();
        for(String s : arr) {
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * Check the inventory after 1 tick and see how many items have been crafted, then add the amount defined by the multiplier
     */
    public static class addExtraItemsLater implements Runnable
    {
        int amountBefore = 0;
        int amountToAdd = 0;
        Material material;
        PlayerInventory inv = null;

        public addExtraItemsLater (PlayerInventory inventory, int amountBefore, Material toCompare, int amountToAdd)
        {
            this.amountBefore = amountBefore;
            this.amountToAdd = amountToAdd;
            material = toCompare;
            inv = inventory;
        }

        @Override
        public void run()
        {
            int amountAfter = countInvItem(inv, material);
            int amountToAdd = (amountAfter - amountBefore) * (this.amountToAdd);
            inv.addItem(new ItemStack(material, amountToAdd));
        }
    }

    @Override
    public void starting()
    {
    }

    @Override
    public void closing()
    {
    }
}
