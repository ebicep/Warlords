package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class Riftbinder extends AbstractMob implements BossMob {

    private static final double PULSE_RADIUS = 7;
    private static final double FRACTURE_RADIUS = 11;
    private static final float FRACTURE_HEALTH = 0.5f;

    private boolean fractureTriggered = false;

    public Riftbinder(Location spawnLocation) {
        this(spawnLocation, "Riftbinder", 16000, 0.3f, 15, 800, 1100);
    }

    public Riftbinder(
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
                new ConduitPulse()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.RIFTBINDER;
    }

    @Override
    public Component getDescription() {
        return Component.text("Binder of the Opex Conduit", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    @Override
    public double getMobScale() {
        return 1.2;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        ChatUtils.sendTitleToGamePlayers(
                option.getGame(),
                Component.text("RIFTBINDER", NamedTextColor.LIGHT_PURPLE),
                Component.text("A conduit binds the rift. Leave the marked circle.", NamedTextColor.GRAY),
                10,
                40,
                10
        );
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (fractureTriggered || warlordsNPC.getCurrentHealth() / warlordsNPC.getMaxHealth() > FRACTURE_HEALTH) {
            return;
        }
        fractureTriggered = true;
        fracture();
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.fromRGB(170, 60, 230))
                .with(FireworkEffect.Type.BALL)
                .build());
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 0.5f);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0.6f);
    }

    private void fracture() {
        Location center = warlordsNPC.getLocation().clone();
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("FRACTURE", NamedTextColor.LIGHT_PURPLE),
                Component.text("Step out of the rift before it snaps!", NamedTextColor.RED),
                5,
                25,
                5
        );
        telegraph(warlordsNPC, center, FRACTURE_RADIUS, 220, 80, 255, () -> release(
                warlordsNPC,
                center,
                FRACTURE_RADIUS,
                "Fracture",
                1100,
                1400,
                35,
                3 * GameRunnable.SECOND,
                -1.4
        ));
    }

    private static void telegraph(WarlordsEntity caster, Location center, double radius, int red, int green, int blue, Runnable onImpact) {
        EffectUtils.playCylinderAnimation(center, radius, red, green, blue, 16, 2);
        Utils.playGlobalSound(center, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.5f, 0.6f);
        new GameRunnable(caster.getGame()) {
            @Override
            public void run() {
                if (caster.isDead()) {
                    return;
                }
                onImpact.run();
            }
        }.runTaskLater(GameRunnable.SECOND);
    }

    private static void release(
            WarlordsEntity caster,
            Location center,
            double radius,
            String cause,
            float minDamage,
            float maxDamage,
            int slowPercent,
            int slowTicks,
            double knockback
    ) {
        EffectUtils.playCylinderAnimation(center, radius, 140, 30, 200, 20, 3);
        Utils.playGlobalSound(center, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 0.8f);
        Utils.playGlobalSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.5f);
        PlayerFilter.entitiesAround(center, radius, radius, radius)
                .aliveEnemiesOf(caster)
                .forEach(enemy -> {
                    Utils.addKnockback(cause, center, enemy, knockback, 0.28);
                    enemy.addSpeedModifier(caster, cause, -slowPercent, slowTicks);
                    enemy.addInstance(InstanceBuilder
                            .damage()
                            .cause(cause)
                            .source(caster)
                            .min(minDamage)
                            .max(maxDamage)
                    );
                });
    }

    private static class ConduitPulse extends AbstractPveAbility {

        public ConduitPulse() {
            super(AbstractAbilityBuilder.create("riftbinderConduitPulse")
                    .pve()
                    .name("Conduit Pulse")
                    .cooldown(10)
                    .energyCost(0));
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            Location center = wp.getLocation().clone();
            telegraph(wp, center, PULSE_RADIUS, 170, 70, 230, () -> {
                EffectUtils.playCylinderAnimation(center, PULSE_RADIUS, 140, 30, 200, 20, 3);
                Utils.playGlobalSound(center, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 1.1f);
                Utils.playGlobalSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.8f);
                PlayerFilter.entitiesAround(center, PULSE_RADIUS, PULSE_RADIUS, PULSE_RADIUS)
                        .aliveEnemiesOf(wp)
                        .forEach(enemy -> {
                            Utils.addKnockback(name, center, enemy, -1.1, 0.22);
                            enemy.addSpeedModifier(wp, name, -20, 2 * GameRunnable.SECOND);
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .min(750)
                                    .max(950)
                            );
                        });
            });
            return true;
        }
    }
}
