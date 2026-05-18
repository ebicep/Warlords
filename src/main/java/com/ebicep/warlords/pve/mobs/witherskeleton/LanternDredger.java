package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class LanternDredger extends AbstractMob implements ChampionMob {

    private static final int LANTERN_RADIUS = 7;
    private static final int LANTERN_REFRESH_INTERVAL_TICKS = 10;
    private static final int LANTERN_GLOW_DURATION_TICKS = 20;
    private static final float DAMAGE_REDUCTION_PERCENT = 30;

    private static final int BLACKOUT_COOLDOWN_TICKS = 15 * 20;
    private static final int BLACKOUT_CAST_TICKS = 2 * 20;
    private static final int BLACKOUT_BLINDNESS_TICKS = 4 * 20;

    private int lanternRefreshTicks = 0;
    private int blackoutCooldownTicks = 5 * 20;
    private int blackoutCastTicks = 0;

    public LanternDredger(Location spawnLocation) {
        super(
                spawnLocation,
                "Lantern Dredger",
                11000,
                0.12f,
                10,
                700,
                1200
        );
    }

    public LanternDredger(
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
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.LANTERN_DREDGER;
    }

    @Override
    public double getMobScale() {
        return 1.1;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_SOUL_SAND_PLACE, 2, 0.6f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        tickLanternAura();
        tickBlackout();
    }

    private void tickLanternAura() {
        lanternRefreshTicks--;

        if (lanternRefreshTicks > 0) {
            return;
        }

        lanternRefreshTicks = LANTERN_REFRESH_INTERVAL_TICKS;

        PlayerFilter.entitiesAround(warlordsNPC, LANTERN_RADIUS, LANTERN_RADIUS, LANTERN_RADIUS)
                .aliveTeammatesOf(warlordsNPC)
                .forEach(this::applyLanternGlow);

        EffectUtils.drawRing(warlordsNPC.getLocation(), LANTERN_RADIUS, 2, Particle.SOUL_FIRE_FLAME);
    }

    private void applyLanternGlow(WarlordsEntity target) {
        Optional<RegularCooldown<LanternGlowData>> existingGlow = getLanternGlow(target);

        if (existingGlow.isPresent()) {
            existingGlow.get().setTicksLeft(LANTERN_GLOW_DURATION_TICKS);
            return;
        }

        LanternGlowData data = new LanternGlowData();

        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Lantern Glow",
                "LG",
                LanternGlowData.class,
                data,
                warlordsNPC,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                LANTERN_GLOW_DURATION_TICKS
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                    "Lantern Glow",
                    1 - DAMAGE_REDUCTION_PERCENT / 100f
            );
        }));
    }

    @SuppressWarnings("unchecked")
    private Optional<RegularCooldown<LanternGlowData>> getLanternGlow(WarlordsEntity target) {
        return (Optional<RegularCooldown<LanternGlowData>>) (Optional<?>) new CooldownFilter<>(target, RegularCooldown.class)
                .filterCooldownClass(LanternGlowData.class)
                .filterCooldownFrom(warlordsNPC)
                .filterName("Lantern Glow")
                .filter(RegularCooldown::hasTicksLeft)
                .findFirst();
    }

    private void tickBlackout() {
        if (blackoutCastTicks > 0) {
            blackoutCastTicks--;

            if (blackoutCastTicks % 10 == 0) {
                playBlackoutChargeEffects();
            }

            if (blackoutCastTicks <= 0) {
                triggerBlackout();
            }

            return;
        }

        blackoutCooldownTicks--;

        if (blackoutCooldownTicks <= 0) {
            startBlackout();
        }
    }

    private void startBlackout() {
        blackoutCastTicks = BLACKOUT_CAST_TICKS;
        blackoutCooldownTicks = BLACKOUT_COOLDOWN_TICKS;

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 2, 0.7f);

        PlayerFilter.entitiesAround(warlordsNPC, LANTERN_RADIUS, LANTERN_RADIUS, LANTERN_RADIUS)
                .aliveEnemiesOf(warlordsNPC)
                .forEach(target -> {
                    target.playSound(target.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 0.6f);
                });
    }

    private void playBlackoutChargeEffects() {
        Location location = warlordsNPC.getLocation().clone().add(0, 1.2, 0);
        warlordsNPC.getWorld().spawnParticle(Particle.SQUID_INK, location, 18, .45, .45, .45, .03);
        EffectUtils.drawRing(warlordsNPC.getLocation(), LANTERN_RADIUS, 2, Particle.SCULK_SOUL);
    }

    private void triggerBlackout() {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 0.5f);

        PlayerFilter.entitiesAround(warlordsNPC, LANTERN_RADIUS, LANTERN_RADIUS, LANTERN_RADIUS)
                .aliveEnemiesOf(warlordsNPC)
                .forEach(target -> {
                    if (target.getEntity() instanceof Player player) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, BLACKOUT_BLINDNESS_TICKS, 0));
                    }

                    target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_HURT, 1, 0.6f);
                });
    }

    private static class LanternGlowData {
    }

}
