package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.paladin.specs.Protector;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HammerOfLight extends AbstractAbility {

    private static final int radius = 6;
    private final int duration = 10;
    private boolean isCrownOfLight = false;

    public HammerOfLight() {
        super("Hammer of Light", 178, 244, 62.64f, 50, 20, 175);
    }

    public static boolean standingInHammer(WarlordsPlayer owner, Entity standing) {
        if (!(owner.getSpec() instanceof Protector)) return false;
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Hammer of Light - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    // in case we use it again
    public static List<WarlordsPlayer> getStandingInHammer(WarlordsPlayer owner) {
        List<WarlordsPlayer> playersInHammer = new ArrayList<>();
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Hammer of Light - " + owner.getName())) {
                for (WarlordsPlayer enemy : PlayerFilter
                        .entitiesAround(entity, radius, 4, radius)
                        .enemiesOf(owner)
                        .isAlive()) {
                    playersInHammer.add(enemy);
                }
                break;
            }
        }
        return playersInHammer;
    }

    public boolean isCrownOfLight() {
        return isCrownOfLight;
    }

    public void setCrownOfLight(boolean crownOfLight) {
        isCrownOfLight = crownOfLight;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw down a Hammer of Light on\n" +
                "§7the ground, dealing §c178 §7- §c244 §7damage\n" +
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
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        if (player.getTargetBlock((Set<Material>) null, 25).getType() == Material.AIR) return false;
        wp.subtractEnergy(energyCost);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));


        Location location = player.getTargetBlock((Set<Material>) null, 25).getLocation().add(1, 0, 1).clone();
        ArmorStand hammer = spawnHammer(location, wp);
        HammerOfLight tempHammerOfLight = new HammerOfLight();

        RegularCooldown<HammerOfLight> hammerOfLightCooldown = new RegularCooldown<>(
                name,
                "HAMMER",
                HammerOfLight.class,
                tempHammerOfLight,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        );

        wp.getCooldownManager().addCooldown(hammerOfLightCooldown);

        Utils.playGlobalSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE),
                new LineEffect(location.clone().add(0, 2.3, 0), ParticleEffect.SPELL)
        );
        BukkitTask task = wp.getGame().registerGameTask(circleEffect::playEffects, 0, 1);

        location.add(0, 1, 0);

        new GameRunnable(wp.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (counter % 20 == 0) {
                    if (tempHammerOfLight.isCrownOfLight()) {
                        if (wp.isAlive()) {
                            PlayerFilter.entitiesAround(wp.getLocation(), radius, radius, radius)
                                    .aliveTeammatesOf(wp)
                                    .forEach(teammate -> teammate.addHealingInstance(
                                            wp,
                                            "Crown of Light",
                                            minDamageHeal * 1.5f,
                                            maxDamageHeal * 1.5f,
                                            critChance,
                                            critMultiplier,
                                            false, false));
                        }
                    } else {
                        for (WarlordsPlayer warlordsPlayer : PlayerFilter
                                .entitiesAround(location, radius, radius, radius)
                                .isAlive()
                        ) {
                            if (wp.isTeammateAlive(warlordsPlayer)) {
                                warlordsPlayer.addHealingInstance(
                                        wp,
                                        name,
                                        minDamageHeal,
                                        maxDamageHeal,
                                        critChance,
                                        critMultiplier,
                                        false, false);
                            } else {
                                warlordsPlayer.addDamageInstance(
                                        wp,
                                        name,
                                        178,
                                        244,
                                        critChance,
                                        critMultiplier,
                                        false);
                            }
                        }
                    }
                }
                if (!wp.getCooldownManager().hasCooldown(hammerOfLightCooldown)) {
                    hammer.remove();
                    task.cancel();
                    this.cancel();
                }
                counter++;
            }

        }.runTaskTimer(0, 0);

        addSecondaryAbility(() -> {
                    if (wp.isAlive() && wp.getCooldownManager().hasCooldown(hammerOfLightCooldown)) {
                        hammer.remove();
                        task.cancel();
                        tempHammerOfLight.setCrownOfLight(true);
                        hammerOfLightCooldown.setNameAbbreviation("CROWN");

                        Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.15f);
                        Utils.playGlobalSound(wp.getLocation(), "mage.firebreath.activation", 2, 0.25f);

                        new GameRunnable(wp.getGame()) {
                            @Override
                            public void run() {
                                double angle = 0;
                                for (int i = 0; i < 9; i++) {
                                    double x = .4 * Math.cos(angle);
                                    double z = .4 * Math.sin(angle);
                                    angle += 40;
                                    Vector v = new Vector(x, 2, z);
                                    Location loc = wp.getLocation().clone().add(v);
                                    ParticleEffect.SPELL.display(0, 0, 0, 0f, 1, loc, 500);
                                }

                                CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), wp.getLocation().add(0, 0.75f, 0), radius / 2f);
                                circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(0.5f));
                                circle.playEffects();

                                if (!wp.getCooldownManager().hasCooldown(hammerOfLightCooldown) || wp.isDead()) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(0, 6);
                    }
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(hammerOfLightCooldown) || wp.isDead()
        );

        return true;
    }

    public ArmorStand spawnHammer(Location location, WarlordsPlayer warlordsPlayer) {
        Location newLocation = location.clone();
        for (int i = 0; i < 10; i++) {
            if (newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                newLocation.add(0, -1, 0);
            }
        }
        newLocation.add(0, -1, 0);

        ArmorStand hammer = (ArmorStand) location.getWorld().spawnEntity(newLocation.clone().add(.25, 1.9, -.25), EntityType.ARMOR_STAND);
        hammer.setMetadata("Hammer of Light - " + warlordsPlayer.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
        hammer.setRightArmPose(new EulerAngle(20.25, 0, 0));
        hammer.setItemInHand(new ItemStack(Material.STRING));
        hammer.setGravity(false);
        hammer.setVisible(false);
        hammer.setMarker(true);

        return hammer;
    }
}