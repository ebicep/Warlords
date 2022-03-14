package com.ebicep.warlords.menu.debugmenu;


import com.ebicep.warlords.abilties.CripplingStrike;
import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.abilties.WoundingStrikeDefender;
import com.ebicep.warlords.game.option.PowerupOption.PowerupType;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static com.ebicep.warlords.player.cooldowns.CooldownTypes.BUFF;
import static com.ebicep.warlords.player.cooldowns.CooldownTypes.DEBUFF;

public enum StatusEffectCooldowns {

    DAMAGE("Damage", new ItemStack(Material.WOOL, 1, (byte) 14), ChatColor.RED, PowerupType.DAMAGE.getClass(), PowerupType.DAMAGE, "DMG", BUFF),
    ENERGY("Energy", new ItemStack(Material.WOOL, 1, (byte) 1), ChatColor.GOLD, PowerupType.ENERGY.getClass(), PowerupType.ENERGY, "ENERGY", BUFF),
    SPEED("Speed", new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.YELLOW, PowerupType.SPEED.getClass(), PowerupType.SPEED, "SPEED", BUFF),
    RESISTANCE("Resistance", new ItemStack(Material.WOOL, 1, (byte) 8), ChatColor.GRAY, null, null, "RES", BUFF),
    CRIPPLING("Crippling", new ItemStack(Material.WOOD_SWORD), ChatColor.DARK_RED, CripplingStrike.class, new CripplingStrike(), "CRIP", DEBUFF),
    WOUNDING_BERS("Wounding Bers", new ItemStack(Material.IRON_SWORD), ChatColor.DARK_RED, WoundingStrikeBerserker.class, new WoundingStrikeBerserker(), "WND", DEBUFF),
    WOUNDING_DEF("Wounding Def", new ItemStack(Material.STONE_SWORD), ChatColor.DARK_RED, WoundingStrikeDefender.class, new WoundingStrikeDefender(), "WND", DEBUFF),

    ;

    public String name;
    public ItemStack itemStack;
    public ChatColor color;

    public Class cooldownClass;
    public Object cooldownObject;
    public String actionBarName;
    public CooldownTypes cooldownType;

    StatusEffectCooldowns(String name, ItemStack itemStack, ChatColor color, Class cooldownClass, Object cooldownObject, String actionBarName, CooldownTypes cooldownType) {
        this.name = name;
        this.itemStack = itemStack;
        this.color = color;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.actionBarName = actionBarName;
        this.cooldownType = cooldownType;
    }
}
