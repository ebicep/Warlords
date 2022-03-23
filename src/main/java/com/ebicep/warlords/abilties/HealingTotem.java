package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class HealingTotem extends AbstractTotemBase {

    private final int radius = 7;
    private final int duration = 6;
    private final int crippleDuration = 6;

    public HealingTotem() {
        super("Healing Totem", 191, 224, 62.64f, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Place a totem on the ground that\n" +
                "§7pulses constantly, healing nearby\n" +
                "§7allies in a §e" + radius + " §7block radius for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + "\n" +
                "§7every second. The healing will gradually\n" +
                "§7increase by §a35% §7(up to 210%) every\n" +
                "§7second. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Pressing SHIFT or re-activating the\n " +
                "ability causes your totem to\n" +
                "§7pulse with immense force, crippling all\n" +
                "§7enemies for §6" + crippleDuration + " §7seconds. Crippled enemies\n" +
                "§7deal §c25% §7less damage.";
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 7);
    }

    @Override
    protected void onTotemStand(ArmorStand totemStand, WarlordsPlayer warlordsPlayer) {
        totemStand.setMetadata("healing-totem-" + warlordsPlayer.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
    }

    @Override
    protected void playSound(Player player, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        RegularCooldown<HealingTotem> healingTotemCooldown = new RegularCooldown<>(
                name,
                "TOTEM",
                HealingTotem.class,
                new HealingTotem(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        );
        wp.getCooldownManager().addCooldown(healingTotemCooldown);
        new GameRunnable(wp.getGame()) {
            int timeLeft = 5;

            @Override
            public void run() {
                if (timeLeft != 0) {
                    Location initParticleLoc = totemStand.getLocation().clone().add(0, 1.6, 0);
                    ParticleEffect.VILLAGER_HAPPY.display(0.4F, 0.2F, 0.4F, 0.05F, 5, initParticleLoc, 500);

                    for (Player player1 : wp.getWorld().getPlayers()) {
                        player1.playSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 0.9f);
                    }

                    Location totemLoc = totemStand.getLocation();
                    totemLoc.add(0, 2, 0);
                    Location particleLoc = totemLoc.clone();
                    for (int i = 0; i < 1; i++) {
                        for (int j = 0; j < 12; j++) {
                            double angle = j / 10D * Math.PI * 2;
                            double width = radius;
                            particleLoc.setX(totemLoc.getX() + Math.sin(angle) * width);
                            particleLoc.setY(totemLoc.getY() + i / 2D);
                            particleLoc.setZ(totemLoc.getZ() + Math.cos(angle) * width);

                            ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, particleLoc, 500);
                        }
                    }

                    CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), totemStand.getLocation().add(0, 1, 0), radius);
                    circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE).particlesPerCircumference(1.5));
                    circle.playEffects();

                    //1
                    //1.35
                    //1.7
                    //2.05
                    //2.4
                    //2.85
                    float healMultiplier = 1 + (.35f * (5 - timeLeft));
                    PlayerFilter.entitiesAround(totemStand, radius, radius, radius)
                            .aliveTeammatesOf(wp)
                            .forEach((nearPlayer) -> {
                                nearPlayer.addHealingInstance(
                                        wp,
                                        name,
                                        minDamageHeal * healMultiplier,
                                        maxDamageHeal * healMultiplier,
                                        critChance,
                                        critMultiplier,
                                        false, false);
                            });
                } else {
                    PlayerFilter.entitiesAround(totemStand, radius, radius, radius)
                            .aliveTeammatesOf(wp)
                            .forEach((nearPlayer) -> {
                                nearPlayer.addHealingInstance(
                                        wp,
                                        name,
                                        minDamageHeal * 3.1f,
                                        maxDamageHeal * 3.1f,
                                        critChance,
                                        critMultiplier,
                                        false, false);
                            });
                    Utils.playGlobalSound(totemStand.getLocation(), Sound.BLAZE_DEATH, 1.2f, 0.7f);
                    Utils.playGlobalSound(totemStand.getLocation(), "shaman.heal.impact", 2, 1);

                    new FallingBlockWaveEffect(totemStand.getLocation().clone().add(0, 1, 0), 3, 0.8, Material.SAPLING, (byte) 1).play();

                    totemStand.remove();
                    this.cancel();
                }
                timeLeft--;
            }

        }.runTaskTimer(0, 20);

        addSecondaryAbility(() -> {
                    PlayerFilter.entitiesAround(totemStand.getLocation(), radius, radius, radius)
                            .aliveEnemiesOf(wp)
                            .forEach((p) -> {
                                wp.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN + ChatColor.GRAY + " Your Healing Totem has crippled " + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + "!");
                                p.getCooldownManager().addCooldown(new RegularCooldown<HealingTotem>(
                                        "Totem Crippling",
                                        "CRIP",
                                        HealingTotem.class,
                                        new HealingTotem(),
                                        wp,
                                        CooldownTypes.DEBUFF,
                                        cooldownManager -> {
                                        },
                                        crippleDuration * 20
                                ) {
                                    @Override
                                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                        return currentDamageValue * .75f;
                                    }
                                });
                            });
                    Utils.playGlobalSound(totemStand.getLocation(), "paladin.hammeroflight.impact", 1.5f, 0.2f);
                    new FallingBlockWaveEffect(totemStand.getLocation().add(0, 1, 0), 7, 2, Material.SAPLING, (byte) 1).play();
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(healingTotemCooldown));
    }


}