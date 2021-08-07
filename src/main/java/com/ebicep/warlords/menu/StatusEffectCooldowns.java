package com.ebicep.warlords.menu;


import com.ebicep.warlords.classes.abilties.CripplingStrike;
import com.ebicep.warlords.classes.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.classes.abilties.WoundingStrikeDefender;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.powerups.DamagePowerUp;
import com.ebicep.warlords.powerups.EnergyPowerUp;
import com.ebicep.warlords.powerups.SpeedPowerUp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static com.ebicep.warlords.player.CooldownTypes.BUFF;
import static com.ebicep.warlords.player.CooldownTypes.DEBUFF;

public enum StatusEffectCooldowns {

    DAMAGE("Damage", new ItemStack(Material.WOOL, 1, (byte) 14), ChatColor.RED, DamagePowerUp.class, new DamagePowerUp(), "DMG", BUFF),
    DAMAGE2("Damage", new ItemStack(Material.WOOL, 1, (byte) 14), ChatColor.RED, DamagePowerUp.class, new DamagePowerUp(), "DMG", BUFF),
    ENERGY("Energy", new ItemStack(Material.WOOL, 1, (byte) 1), ChatColor.GOLD, EnergyPowerUp.class, new EnergyPowerUp(), "ENERGY", BUFF),
    SPEED("Speed", new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.YELLOW, SpeedPowerUp.class, new SpeedPowerUp(), "SPEED", BUFF),
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
