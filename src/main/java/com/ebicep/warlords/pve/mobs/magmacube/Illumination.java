package com.ebicep.warlords.pve.mobs.magmacube;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SlimeSize;
import org.bukkit.*;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class Illumination extends AbstractMob implements AdvancedMob {

    public Illumination(Location spawnLocation) {
        super(
                spawnLocation,
                "Illumination",
                4000,
                0.15f,
                20,
                0,
                0,
                new LastStandNear()
        );
    }

    public Illumination(
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
                new LastStandNear()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ILLUMINATION;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        npc.getOrAddTrait(SlimeSize.class).setSize(7);
        npc.data().set(NPC.Metadata.JUMP_POWER_SUPPLIER, (Function<NPC, Float>) npc -> .05f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we != null) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(we, 5, 5, 5)
                    .aliveEnemiesOf(we)
            ) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Blight")
                        .source(we)
                        .min(900)
                        .max(1200)
                );
            }
        }

        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.RED)
                                                                       .with(FireworkEffect.Type.BALL_LARGE)
                                                                       .withTrail()
                                                                       .build());
        EffectUtils.playHelixAnimation(deathLocation, 6, 255, 40, 40);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_SCREAM, 1, 2);
    }

    private static class LastStandNear extends AbstractPveAbility {

        public LastStandNear() {
            super("Last Stand Near", 3, 100);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {


            wp.getCooldownManager().removeCooldown(LastStand.class, false);

            Location loc = wp.getLocation();
            EffectUtils.playSphereAnimation(loc, 9, Particle.SPELL, 1);
            Utils.playGlobalSound(loc, "warrior.laststand.activation", 2, 0.6f);
            for (WarlordsEntity ally : PlayerFilter
                    .entitiesAround(wp, 9, 9, 9)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                if (!ally.getName().equals("Illumination")) {
                    ally.getCooldownManager().removeCooldown(LastStand.class, false);
                    ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                            name,
                            "",
                            LastStand.class,
                            new LastStand(),
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            3 * 20
                    ) {
                        @Override
                        public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * .5f;
                        }
                    });
                }
            }
            return true;
        }
    }
}
