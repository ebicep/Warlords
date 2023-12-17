package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.EnumSet;

public class EventNecronomiconGrimoire extends AbstractMob implements BossMinionMob {

    private int smiteTickCooldown = 0;
    private int timesSmited = 0;

    public EventNecronomiconGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Necronomicon Grimoire",
                10000,
                0f,
                10,
                0,
                0
        );
    }

    public EventNecronomiconGrimoire(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_NECRONOMICON_GRIMOIRE;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (smiteTickCooldown > 0) {
            smiteTickCooldown--;
            if (smiteTickCooldown == 0) {
                smite();
            }
        }
    }

    private void smite() {
        timesSmited++;
        if (timesSmited == 2) {
            pveOption.despawnMob(this);
        }
        Entity target = getTarget();
        if (target == null) {
            return;
        }
        WarlordsEntity targetWarlordsEntity = Warlords.getPlayer(target);
        if (targetWarlordsEntity == null) {
            return;
        }
        targetWarlordsEntity.addDamageInstance(
                warlordsNPC,
                "Smite",
                5000,
                5000,
                0,
                100,
                EnumSet.of(InstanceFlags.TRUE_DAMAGE)
        ).ifPresent(event -> {
            if (!event.isDead()) {
                smiteTickCooldown = 10 * 20;
                return;
            }
            warlordsNPC.addHealingInstance(
                    warlordsNPC,
                    "Smite Kill",
                    2500,
                    2500,
                    0,
                    100
            );
            smiteTickCooldown = 5 * 20;
        });
        EffectUtils.strikeLightning(target.getLocation(), false);
    }
}
