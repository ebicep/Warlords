package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractChain;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoulfireBlast extends AbstractAbility {

    private int maxRange = 30;
    private int speedBuff = 40;
    private int speedTickDuration = 60;

    public SoulfireBlast() {
        super("Soulfire Blast", 506, 685, 10, 60, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Unleash a concentrated beam of demonic power, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to all enemies hit. Enemies hit are slightly pushed back. " +
                                       "If the target is affected by 3 stacks of Poisonous Hex, remove all stacks, " +
                                       "increase the damage dealt of Soulfire Blast by 100% and reduce the cooldown of Energy Seer by 1 second. Gain "))
                               .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" speed for "))
                               .append(Component.text(format(speedTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds.\n\nHas a maximum range of "))
                               .append(Component.text(format(30), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        Location location = player.getTargetBlock(null, maxRange).getLocation().clone().add(.5, 1, .5).clone();
        List<ArmorStand> beam = AbstractChain.spawnChain(location, wp.getLocation(), new ItemStack(Material.GRAY_STAINED_GLASS));
        Set<WarlordsEntity> enemies = new HashSet<>();
        for (ArmorStand armorStand : beam) {
            PlayerFilter.entitiesAround(armorStand.getLocation().add(0, .5, 0), 1.1, 1.2, 1.1)
                        .enemiesOf(wp)
                        .forEach(enemies::add);
        }

        for (WarlordsEntity enemy : enemies) {
            float minDamage = minDamageHeal;
            float maxDamage = maxDamageHeal;
            int hexStacks = (int) new CooldownFilter<>(enemy, RegularCooldown.class)
                    .filterCooldownFrom(wp)
                    .filterCooldownClass(PoisonousHex.class)
                    .stream()
                    .count();
            if (hexStacks >= 3) {
                enemy.getCooldownManager().removeCooldown(PoisonousHex.class, false);
                minDamage *= 2;
                maxDamage *= 2;
                wp.subtractPurpleCooldown(1);
            }
            enemy.addDamageInstance(wp, name, minDamage, maxDamage, critChance, critMultiplier, false);
        }
        return true;
    }
}
