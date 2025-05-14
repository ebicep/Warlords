package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;

import java.util.Collections;

public class TDPiglin extends TowerDefenseMob implements BasicMob {

    public TDPiglin(Location spawnLocation) {
        this(
                spawnLocation,
                "Piglin",
                200,
                .4f,
                0,
                100,
                100
        );
    }

    public TDPiglin(
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
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 10 != 0) {
            return;
        }
        boolean inGroup = PlayerFilter.entitiesAround(warlordsNPC, 5, 5, 5)
                                      .aliveTeammatesOf(warlordsNPC)
                                      .stream()
                                      .count() >= 5;
        getPhysicalResistance().removeModifier("Group Bonus");
        warlordsNPC.getCooldownManager().removeCooldownByName("Group Bonus");
        if (!inGroup) {
            return;
        }
        getPhysicalResistance().addAdditiveModifier("Group Bonus", 20, 11);
        warlordsNPC.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Group Bonus",
                null,
                TDPiglin.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                11 * 20,
                Collections.singletonList((cooldown, ticksLeft, cdTicksElapsed) -> {
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.3f;
            }
        });
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_PIGLIN;
    }

}
