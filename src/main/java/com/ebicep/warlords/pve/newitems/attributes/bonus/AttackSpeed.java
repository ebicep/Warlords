package com.ebicep.warlords.pve.newitems.attributes.bonus;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AttackSpeed implements Attribute {

    private static final ItemStack ITEM_STACK = new ItemBuilder(Material.IRON_SWORD)
            .get();

    @Override
    public String getDatabaseName() {
        return "ATTACK_SPEED";
    }

    @Override
    public String getName() {
        return "Attack Speed";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.RED;
    }

    @Override
    public ItemStack getItemStack() {
        return ITEM_STACK;
    }

    @Override
    public Component formatValue(float value, String prefix) {
        return ComponentBuilder
                .create()
                .text(prefix + NumberFormat.formatOptionalTenths(value) + "% ", getTextColor())
                .text(getName(), NamedTextColor.GRAY)
                .build();
    }

    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        // lower melee cooldown = faster attacks
        warlordsPlayer.getPveHitCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Item", -value / 100f);
    }

}
