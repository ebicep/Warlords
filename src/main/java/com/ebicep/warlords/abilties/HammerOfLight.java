package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;

public class HammerOfLight extends AbstractAbility {
    private boolean pveUpgrade = false;

    protected int playersHealed = 0;
    protected int playersDamaged = 0;
    private boolean isCrownOfLight = false;
    private Location location;

    private static final int radius = 6;
    private int duration = 10;
    private float minDamage = 178;
    private float maxDamage = 244;

    private float amountHealed = 0;

    public HammerOfLight() {
        super("Hammer of Light", 178, 244, 62.64f, 50, 20, 175);
    }

    public HammerOfLight(Location location) {
        super("Hammer of Light", 178, 244, 62.64f, 50, 20, 175);
        this.location = location;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw down a Hammer of Light on\n" +
                "§7the ground, dealing §c" + format(minDamage) + " §7- §c" + format(maxDamage) + "§7damage\n" +
                "§7damage every second to nearby enemies and\n" +
                "§7healing nearby allies for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7every second\n" +
                "§7in a §e" + radius + " §7block radius. Your Protector Strike\n" +
                "§7pierces shields and defenses of enemies\n" +
                "§7standing on top of the Hammer of Light.\n" +
                "§7Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7You may SNEAK to turn your hammer into Crown of Light.\n" +
                "§7Removing the damage and piercing BUT increasing\n" +
                "§7the healing §7by §a50% §7and reducing the\n" +
                "§7energy cost of your Protector's Strike by\n" +
                "§e10 §7energy. You cannot put the Hammer of Light\n" +
                "§7back down after you converted it.";
        description = WordWrap.wrapWithNewline(ChatColor.GRAY +
                        "§7Throw down a Hammer of Light on\n" +
                        "§7the ground, dealing §c" + format(minDamage) + " §7- §c" + format(maxDamage) + "§7damage\n" +
                        "§7damage every second to nearby enemies and\n" +
                        "§7healing nearby allies for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7every second\n" +
                        "§7in a §e" + radius + " §7block radius. Your Protector Strike\n" +
                        "§7pierces shields and defenses of enemies\n" +
                        "§7standing on top of the Hammer of Light.\n" +
                        "§7Lasts §6" + duration + " §7seconds." +
                        "\n\n" +
                        "§7You may SNEAK to turn your hammer into Crown of Light.\n" +
                        "§7Removing the damage and piercing BUT increasing\n" +
                        "§7the healing §7by §a50% §7and reducing the\n" +
                        "§7energy cost of your Protector's Strike by\n" +
                        "§e10 §7energy. You cannot put the Hammer of Light\n" +
                        "§7back down after you converted it.",
                DESCRIPTION_WIDTH
        );
    }

    public static boolean isStandingInHammer(WarlordsEntity owner, WarlordsEntity standing) {
        return new CooldownFilter<>(owner, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                .filter(HammerOfLight::isHammer)
                .anyMatch(hammerOfLight -> hammerOfLight.getLocation().distanceSquared(standing.getLocation()) < radius * radius);
    }

    public static List<WarlordsEntity> getStandingInHammer(WarlordsEntity owner) {
        Set<WarlordsEntity> playersInHammer = new HashSet<>();
        new CooldownFilter<>(owner, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                .filter(HammerOfLight::isHammer)
                .map(HammerOfLight::getLocation)
                .forEach(loc -> {
                    for (WarlordsEntity enemy : PlayerFilter
                            .entitiesAround(loc, radius, 4, radius)
                            .enemiesOf(owner)
                            .isAlive()) {
                        playersInHammer.add(enemy);
                    }
                });
        return new ArrayList<>(playersInHammer);
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Players Damaged", "" + playersDamaged));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        if (player.getTargetBlock((Set<Material>) null, 25).getType() == Material.AIR) {
            return false;
        }
        wp.subtractEnergy(energyCost, false);
        wp.setOrangeCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
        Utils.playGlobalSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);


        Location location = player.getTargetBlock((Set<Material>) null, 25).getLocation().clone().add(.6, 0, .6).clone();
        if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
            if (location.clone().add(1, 0, 0).getBlock().getType() == Material.AIR) {
                location.add(.6, 0, 0);
            } else if (location.clone().add(-1, 0, 0).getBlock().getType() == Material.AIR) {
                location.add(-.6, 0, 0);
            } else if (location.clone().add(0, 0, 1).getBlock().getType() == Material.AIR) {
                location.add(0, 0, .6);
            } else if (location.clone().add(0, 0, -1).getBlock().getType() == Material.AIR) {
                location.add(0, 0, -.6);
            }
        }

        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE),
                new LineEffect(location.clone().add(0, 2.3, 0), ParticleEffect.SPELL)
        );
        BukkitTask particleTask = wp.getGame().registerGameTask(circleEffect::playEffects, 0, 1);
        ArmorStand hammer = spawnHammer(location);

        HammerOfLight tempHammerOfLight = new HammerOfLight(location);
        RegularCooldown<HammerOfLight> hammerOfLightCooldown = new RegularCooldown<>(
                name,
                "HAMMER",
                HammerOfLight.class,
                tempHammerOfLight,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    hammer.remove();
                    particleTask.cancel();
                },
                false,
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        if (tempHammerOfLight.isCrownOfLight()) {
                            if (wp.isAlive()) {
                                for (WarlordsEntity allyTarget : PlayerFilter
                                        .entitiesAround(wp.getLocation(), radius, radius, radius)
                                        .aliveTeammatesOf(wp)
                                ) {
                                    playersHealed++;
                                    allyTarget.addHealingInstance(
                                            wp,
                                            "Crown of Light",
                                            minDamageHeal * 1.5f,
                                            maxDamageHeal * 1.5f,
                                            critChance,
                                            critMultiplier,
                                            false,
                                            false
                                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                        tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                    });
                                }
                            }
                        } else {
                            for (WarlordsEntity hammerTarget : PlayerFilter
                                    .entitiesAround(location, radius, radius, radius)
                                    .isAlive()
                            ) {
                                if (wp.isTeammate(hammerTarget)) {
                                    playersHealed++;
                                    hammerTarget.addHealingInstance(
                                            wp,
                                            name,
                                            minDamageHeal,
                                            maxDamageHeal,
                                            critChance,
                                            critMultiplier,
                                            false,
                                            false
                                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                        tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                    });
                                } else {
                                    playersDamaged++;
                                    hammerTarget.addDamageInstance(
                                            wp,
                                            name,
                                            minDamage,
                                            maxDamage,
                                            critChance,
                                            critMultiplier,
                                            false
                                    );
                                }
                            }
                        }
                    }
                })
        );

        wp.getCooldownManager().addCooldown(hammerOfLightCooldown);

        location.add(0, 1, 0);

        addSecondaryAbility(() -> {
                    if (wp.isAlive() && wp.getCooldownManager().hasCooldown(hammerOfLightCooldown)) {
                        hammer.remove();
                        particleTask.cancel();

                        Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.15f);
                        Utils.playGlobalSound(wp.getLocation(), "mage.firebreath.activation", 2, 0.25f);

                        hammerOfLightCooldown.setRemoveOnDeath(true);
                        hammerOfLightCooldown.addTriConsumer((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 6 == 0) {
                                double angle = 0;
                                for (int i = 0; i < 9; i++) {
                                    double x = .4 * Math.cos(angle);
                                    double z = .4 * Math.sin(angle);
                                    angle += 40;
                                    Vector v = new Vector(x, 2, z);
                                    Location loc = wp.getLocation().clone().add(v);
                                    ParticleEffect.SPELL.display(0, 0, 0, 0f, 1, loc, 500);
                                }

                                new CircleEffect(
                                        wp.getGame(),
                                        wp.getTeam(),
                                        wp.getLocation().add(0, 0.75f, 0),
                                        radius / 2f,
                                        new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(0.5f)
                                ).playEffects();
                            }
                        });


                        tempHammerOfLight.setCrownOfLight(true);
                        hammerOfLightCooldown.setNameAbbreviation("CROWN");

                        if (pveUpgrade) {
                            pulseHeal(wp, 20, 1.5, tempHammerOfLight);
                            pulseHeal(wp, 40, 2, tempHammerOfLight);
                            pulseHeal(wp, 60, 2.5, tempHammerOfLight);
                        }
                    }
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(hammerOfLightCooldown) || wp.isDead()
        );

        return true;
    }

    private void pulseHeal(WarlordsEntity wp, int delay, double radiusMultiplier, HammerOfLight tempHammerOfLight) {
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.4f);
                EffectUtils.strikeLightning(wp.getLocation(), false, delay / 10);
                EffectUtils.playHelixAnimation(wp.getLocation(), radius * radiusMultiplier, ParticleEffect.SPELL_WITCH, 1, 20);
                new CircleEffect(
                        wp.getGame(),
                        wp.getTeam(),
                        wp.getLocation().add(0, 0.75f, 0),
                        radius * radiusMultiplier,
                        new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(1)
                ).playEffects();

                for (WarlordsEntity allyTarget : PlayerFilter
                        .entitiesAround(wp.getLocation(), radius * radiusMultiplier, radius * radiusMultiplier, radius * radiusMultiplier)
                        .aliveTeammatesOf(wp)
                ) {
                    playersHealed++;
                    allyTarget.addHealingInstance(
                            wp,
                            "Hammer of Illusion",
                            minDamageHeal * 5,
                            maxDamageHeal * 5,
                            critChance,
                            critMultiplier,
                            false,
                            false
                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                        tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                    });
                }

                for (WarlordsEntity enemyTarget : PlayerFilter
                        .entitiesAround(wp.getLocation(), radius * radiusMultiplier, radius * radiusMultiplier, radius * radiusMultiplier)
                        .aliveEnemiesOf(wp)
                ) {
                    enemyTarget.addDamageInstance(
                            wp,
                            "Hammer of Illusion",
                            minDamage * 5,
                            maxDamage * 5,
                            critChance,
                            critMultiplier,
                            false
                    );
                }
            }
        }.runTaskLater(delay);
    }

    public ArmorStand spawnHammer(Location location) {
        Location newLocation = location.clone();
        for (int i = 0; i < 10; i++) {
            if (newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                newLocation.add(0, -1, 0);
            }
        }
        newLocation.add(0, -1, 0);

        ArmorStand hammer = (ArmorStand) location.getWorld().spawnEntity(newLocation.clone().add(.25, 1.9, -.25), EntityType.ARMOR_STAND);
        //hammer.setMetadata("Hammer of Light - " + warlordsPlayer.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
        hammer.setRightArmPose(new EulerAngle(20.25, 0, 0));
        hammer.setItemInHand(new ItemStack(Material.STRING));
        hammer.setGravity(false);
        hammer.setVisible(false);
        hammer.setMarker(true);

        return hammer;
    }

    public boolean isHammer() {
        return !isCrownOfLight;
    }

    public boolean isCrownOfLight() {
        return isCrownOfLight;
    }

    public void setCrownOfLight(boolean crownOfLight) {
        isCrownOfLight = crownOfLight;
    }

    public Location getLocation() {
        return location;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(float minDamage) {
        this.minDamage = minDamage;
    }

    public float getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(float maxDamage) {
        this.maxDamage = maxDamage;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public void addAmountHealed(float amount) {
        this.amountHealed += amount;
    }

    public float getAmountHealed() {
        return amountHealed;
    }
}