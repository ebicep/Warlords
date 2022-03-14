package com.ebicep.warlords.util;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ItemBuilderTest {

    @Test
    public void testNameSetable() {
        ItemBuilder builder = new ItemBuilder(Material.STONE);
        
        builder.name("test");
        
        ItemStack item = builder.get();
        assertEquals("test", item.getItemMeta().getDisplayName());
    }

    @Test
    public void testLoreSetable() {
        ItemBuilder builder = new ItemBuilder(Material.STONE);
        
        builder.lore("test");
        
        ItemStack item = builder.get();
        assertEquals(Arrays.asList("test"), item.getItemMeta().getLore());
    }

    @Test
    public void testLoreSetableWithNewlines() {
        ItemBuilder builder = new ItemBuilder(Material.STONE);
        
        builder.lore("test\ntest1");
        
        ItemStack item = builder.get();
        assertEquals(Arrays.asList("test", "test1"), item.getItemMeta().getLore());
    }

    @Test
    public void testLoreSetableWithNewlinesAndColors() {
        ItemBuilder builder = new ItemBuilder(Material.STONE);
        
        builder.lore(ChatColor.RED + "test\ntest1");
        
        ItemStack item = builder.get();
        assertEquals(Arrays.asList(ChatColor.RED + "test", ChatColor.RED + "test1"), item.getItemMeta().getLore());
    }
    
}
