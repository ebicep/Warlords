package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class NineCrystal extends AbstractMob implements BossMinionMob {

    private SpecType spec;
    private WarlordsEntity owner;

    public NineCrystal(Location spawnLocation, WarlordsEntity owner, SpecType spec) {
        super(
                spawnLocation,
                ChatColor.BOLD + spec.name(),
                1900,
                0,
                0,
                0,
                0
        );
        this.owner = owner;
        this.spec = spec;
    }

    public NineCrystal(Location spawnLocation) {
        super(
                spawnLocation,
                "Pylon",
                2000,
                0,
                0,
                0,
                0
        );
    }

    public NineCrystal(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.NINE_CRYSTAL;
    }

    @Override
    public void onSpawn(PveOption option) {
//        warlordsNPC.getEntity().setAI(false);
        warlordsNPC.getCooldownManager().removeCooldown(DamageCheck.class, false);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (spec == event.getSource().getSpecClass().specType) {
                    return currentDamageValue * 4;
                } else {
                    return currentDamageValue * 0.1f;
                }
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playParticleLinkAnimation(warlordsNPC.getLocation(), new Location(warlordsNPC.getWorld(), 112.5, 13, 62.5), Particle.CHERRY_LEAVES);
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {

    }

    @Override
    public double weaponDropRate() {
        return 0;
    }

    @Override
    public int commonWeaponDropChance() {
        return 0;
    }

    @Override
    public int rareWeaponDropChance() {
        return 0;
    }

    @Override
    public int epicWeaponDropChance() {
        return 0;
    }
}
