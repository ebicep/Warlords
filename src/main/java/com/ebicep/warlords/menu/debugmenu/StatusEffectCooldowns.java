package com.ebicep.warlords.menu.debugmenu;


import com.ebicep.warlords.abilties.CripplingStrike;
import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.abilties.WoundingStrikeDefender;
import com.ebicep.warlords.abilties.internal.DamagePowerup;
import com.ebicep.warlords.abilties.internal.EnergyPowerup;
import com.ebicep.warlords.abilties.internal.SpeedPowerup;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes.BUFF;
import static com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes.DEBUFF;

public enum StatusEffectCooldowns {

    DAMAGE("Damage", new ItemStack(Material.RED_WOOL), ChatColor.RED, DamagePowerup.class, DamagePowerup.DAMAGE_POWERUP, "DMG", BUFF),
    ENERGY("Energy", new ItemStack(Material.ORANGE_WOOL), ChatColor.GOLD, EnergyPowerup.class, EnergyPowerup.ENERGY_POWERUP, "ENERGY", BUFF),
    SPEED("Speed", new ItemStack(Material.YELLOW_WOOL), ChatColor.YELLOW, SpeedPowerup.class, SpeedPowerup.SPEED_POWERUP, "SPEED", BUFF),
    RESISTANCE("Resistance", new ItemStack(Material.LIGHT_GRAY_WOOL), ChatColor.GRAY, null, null, "RES", BUFF),
    CRIPPLING("Crippling", new ItemStack(Material.WOODEN_SWORD), ChatColor.DARK_RED, CripplingStrike.class, new CripplingStrike(), "CRIP", DEBUFF),
    WOUNDING_BERS("Wounding Bers",
            new ItemStack(Material.IRON_SWORD),
            ChatColor.DARK_RED,
            WoundingStrikeBerserker.class,
            new WoundingStrikeBerserker(),
            "WND",
            DEBUFF
    ),
    WOUNDING_DEF("Wounding Def",
            new ItemStack(Material.STONE_SWORD),
            ChatColor.DARK_RED,
            WoundingStrikeDefender.class,
            new WoundingStrikeDefender(),
            "WND",
            DEBUFF
    ),

    ;

    public final String name;
    public final ItemStack itemStack;
    public final ChatColor color;

    public final Class cooldownClass;
    public final Object cooldownObject;
    public final String actionBarName;
    public final CooldownTypes cooldownType;

    StatusEffectCooldowns(
            String name,
            ItemStack itemStack,
            ChatColor color,
            Class cooldownClass,
            Object cooldownObject,
            String actionBarName,
            CooldownTypes cooldownType
    ) {
        this.name = name;
        this.itemStack = itemStack;
        this.color = color;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.actionBarName = actionBarName;
        this.cooldownType = cooldownType;
    }
}
