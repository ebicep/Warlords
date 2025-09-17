package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.OrbitingSwordsManager;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class SkeletonEnd extends AbstractMob implements ChampionMob {

    public SkeletonEnd(Location spawnLocation) {
        super(
                spawnLocation,
                "Sculk Huntsman",
                8000,
                0.05f,
                30,
                0,
                0,
                new PoisonousHex(AbstractAbilityBuilder.create("skeletonEndPoisonousHex").pve())
        );
    }

    public SkeletonEnd(
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
                new PoisonousHex(AbstractAbilityBuilder.create("skeletonEndPoisonousHex").pve())
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKELETON_END;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.playFirework(
                warlordsNPC.getLocation(),
                FireworkEffect.builder()
                        .withColor(Color.PURPLE)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .build()
        );
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 0.7f);
        receiver.addSpeedModifier(attacker, "End Slowness", -20, 2 * 20);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_SCREAM, 2, 0.4f);
    }
}

