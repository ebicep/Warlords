package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AdvancedVoidShred;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class SkeletalMesmer extends AbstractMob implements EliteMob {

    private static final float voidRadius = 7;

    public SkeletalMesmer(Location spawnLocation) {
        super(
                spawnLocation,
                "Skeletal Mesmer",
                5500,
                0.05f,
                10,
                0,
                0,
                new Fireball(5.5f),
                new FlameBurst(20) {{
                    this.getDamageValues().getFlameBurstDamage().critChance().setBaseValue(0);
                }},
                new AdvancedVoidShred(450, 900, 5, -30, voidRadius, 30)
        );
    }

    public SkeletalMesmer(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new Fireball(5.5f),
                new FlameBurst(20) {{
                    this.getDamageValues().getFlameBurstDamage().critChance().setBaseValue(0);
                }},
                new AdvancedVoidShred(450, 900, 5, -30, voidRadius, 30)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKELETAL_MESMER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.BURST)
                                                                       .withTrail()
                                                                       .build());
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_SKELETON_DEATH, 2, 0.2f);
    }
}
