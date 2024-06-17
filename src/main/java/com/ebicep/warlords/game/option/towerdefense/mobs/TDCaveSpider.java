package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

import java.util.Collections;

public class TDCaveSpider extends TowerDefenseMob implements BasicMob {

    public static void poison(WarlordsEntity from, WarlordsEntity to, int tickDuration) {
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, PoisonCooldown.class, 1);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Poison",
                "POI",
                PoisonCooldown.class,
                new PoisonCooldown() {},
                from,
                CooldownTypes.DEBUFF,
                cooldownManager -> {

                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        to.addDamageInstance(from, "Poison", 50, 50, 0, 100, InstanceFlags.CANT_KILL);
                    }
                })
        ));
    }

    public TDCaveSpider(Location spawnLocation) {
        this(
                spawnLocation,
                "Cave Spider",
                60,
                .5f,
                0,
                100,
                100
        );
    }

    public TDCaveSpider(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        super.onAttack(attacker, receiver, event);
        poison(attacker, receiver, 20 * 10);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_CAVE_SPIDER;
    }

    public interface PoisonCooldown {

    }

}