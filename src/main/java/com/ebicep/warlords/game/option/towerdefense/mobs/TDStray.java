package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;

public class TDStray extends TowerDefenseMob implements BasicMob {

    public TDStray(Location spawnLocation) {
        this(
                spawnLocation,
                "Stray",
                150,
                .3f,
                0,
                100,
                100
        );
    }

    public TDStray(
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
        event.getCustomFlags().add(new CustomInstanceFlags.Valued(
                floatModifiable -> floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, .5f, 0),
                CustomInstanceFlags.Valued.Flag.TD_DEFENDER_ARMOR
        ));
        event.getCustomFlags().add(new CustomInstanceFlags.Valued(
                floatModifiable -> floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, .5f, 0),
                CustomInstanceFlags.Valued.Flag.TD_DEFENDER_ARMOR
        ));
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_STRAY;
    }

}
