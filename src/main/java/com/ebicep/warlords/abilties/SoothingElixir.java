package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoothingElixir extends AbstractAbility {

    private static final double SPEED = 0.220;
    private static final double GRAVITY = -0.008;

    public int playersHealed = 0;

    private final int puddleMinDamage = 235;
    private final int puddleMaxDamage = 342;
    private float puddleRadius = 5;
    private int puddleDuration = 4;
    private int puddleMinHealing = 158;
    private int puddleMaxHealing = 204;

    public SoothingElixir() {
        super("Soothing Elixir", 551, 648, 7, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Throw a short range elixir bottle. The bottle will shatter upon impact, healing nearby allies for" +
                formatRangeHealing(minDamageHeal, maxDamageHeal) + "health and damage nearby enemies for" +
                formatRangeDamage(puddleMinDamage, puddleMaxDamage) + "damage. The projectile will form a small puddle that heals allies for" +
                formatRangeHealing(puddleMinHealing, puddleMaxHealing) + "health per second. Lasts §6" + puddleDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);

        Location location = player.getLocation();
        Vector speed = player.getLocation().getDirection().multiply(SPEED);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setHelmet(new ItemStack(Material.PINK_STAINED_GLASS));
        stand.setGravity(false);
        stand.setVisible(false);
        new GameRunnable(wp.getGame()) {
            int timer = 0;

            @Override
            public void run() {
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(true);
            }

            private void quarterStep(boolean last) {

                if (!stand.isValid()) {
                    this.cancel();
                    return;
                }

                speed.add(new Vector(0, GRAVITY * SPEED, 0));
                Location newLoc = stand.getLocation();
                newLoc.add(speed);
                stand.teleport(newLoc);
                newLoc.add(0, 1.75, 0);

                stand.setHeadPose(new EulerAngle(-speed.getY() * 3, 0, 0));

                boolean shouldExplode;

                timer++;
                if (last) {
                    Matrix4d center = new Matrix4d(newLoc);
                    for (float i = 0; i < 6; i++) {
                        double angle = Math.toRadians(i * 90) + timer * 0.3;
                        double width = 0.3D;
                        ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, 2,
                                center.translateVector(newLoc.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width), 500
                        );
                    }
                }

                WarlordsEntity directHit;
                if (
                        !newLoc.getBlock().isEmpty()
                                && newLoc.getBlock().getType() != Material.GRASS
                                && newLoc.getBlock().getType() != Material.BARRIER
                                && newLoc.getBlock().getType() != Material.VINE
                ) {
                    // Explode based on collision
                    shouldExplode = true;
                } else {
                    directHit = PlayerFilter
                            .entitiesAroundRectangle(newLoc, 1, 2, 1)
                            .aliveTeammatesOfExcludingSelf(wp)
                            .findFirstOrNull();
                    shouldExplode = directHit != null;
                }

                if (shouldExplode) {
                    stand.remove();
                    Utils.playGlobalSound(newLoc, "rogue.healingremedy.impact", 1.5f, 0.1f);
                    Utils.playGlobalSound(newLoc, Sound.BLOCK_GLASS_BREAK, 1.5f, 0.7f);
                    Utils.playGlobalSound(newLoc, "mage.waterbolt.impact", 1.5f, 0.3f);

                    CircleEffect circleEffect = new CircleEffect(
                            wp.getGame(),
                            wp.getTeam(),
                            newLoc,
                            puddleRadius,
                            new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE),
                            new AreaEffect(1, ParticleEffect.DRIP_WATER).particlesPerSurface(0.025)
                    );
                    BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circleEffect::playEffects, 0, 1);
                    wp.getGame().registerGameTask(particleTask);

                    FireWorkEffectPlayer.playFirework(newLoc, FireworkEffect.builder()
                            .withColor(Color.WHITE)
                            .with(FireworkEffect.Type.BURST)
                            .build());

                    for (WarlordsEntity nearEntity : PlayerFilter
                            .entitiesAround(newLoc, puddleRadius, puddleRadius, puddleRadius)
                            .aliveTeammatesOf(wp)
                    ) {
                        playersHealed++;
                        nearEntity.addHealingInstance(
                                wp,
                                name,
                                minDamageHeal,
                                maxDamageHeal,
                                critChance,
                                critMultiplier,
                                false,
                                false
                        );
                    }

                    new GameRunnable(wp.getGame()) {
                        int timeLeft = puddleDuration;

                        @Override
                        public void run() {
                            PlayerFilter.entitiesAround(newLoc, puddleRadius, puddleRadius, puddleRadius)
                                    .aliveTeammatesOf(wp)
                                    .forEach((ally) -> ally.addHealingInstance(
                                            wp,
                                            name,
                                            puddleMinHealing,
                                            puddleMaxHealing,
                                            critChance,
                                            critMultiplier,
                                            false,
                                            false
                                    ));

                            timeLeft--;

                            if (timeLeft <= 0) {
                                this.cancel();
                                particleTask.cancel();
                            }
                        }

                    }.runTaskTimer(20, pveUpgrade ? 10 : 20);

                    for (WarlordsEntity nearEntity : PlayerFilter
                            .entitiesAround(newLoc, puddleRadius, puddleRadius, puddleRadius)
                            .aliveEnemiesOf(wp)
                    ) {
                        Utils.playGlobalSound(nearEntity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.5f);
                        nearEntity.addDamageInstance(
                                wp,
                                name,
                                puddleMinDamage,
                                puddleMaxDamage,
                                critChance,
                                critMultiplier,
                                false
                        );
                        
                        if (pveUpgrade) {
                            ImpalingStrike.giveLeechCooldown(
                                    wp,
                                    nearEntity,
                                    5,
                                    0.25f,
                                    0.15f,
                                    warlordsDamageHealingFinalEvent -> {
                                    }
                            );
                        }
                    }

                    this.cancel();
                }
            }

        }.runTaskTimer(0, 1);

        Utils.playGlobalSound(player.getLocation(), "mage.frostbolt.activation", 2, 0.7f);

        return true;
    }

    public float getPuddleRadius() {
        return puddleRadius;
    }

    public void setPuddleRadius(float puddleRadius) {
        this.puddleRadius = puddleRadius;
    }

    public int getPuddleMinHealing() {
        return puddleMinHealing;
    }

    public void setPuddleMinHealing(int puddleMinHealing) {
        this.puddleMinHealing = puddleMinHealing;
    }

    public int getPuddleMaxHealing() {
        return puddleMaxHealing;
    }

    public void setPuddleMaxHealing(int puddleMaxHealing) {
        this.puddleMaxHealing = puddleMaxHealing;
    }

    public int getPuddleDuration() {
        return puddleDuration;
    }

    public void setPuddleDuration(int puddleDuration) {
        this.puddleDuration = puddleDuration;
    }
}