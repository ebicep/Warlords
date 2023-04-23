package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class UndyingArmy extends AbstractAbility implements Duration {
    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(Component.text("Instant Kill", NamedTextColor.RED))
            .loreLEGACY("§7Right-click this item to die\n§7instantly instead of waiting for\n§7the decay.")
            .get();

    public int playersArmied = 0;

    private final HashMap<WarlordsEntity, Boolean> playersPopped = new HashMap<>();
    private int radius = 15;
    private int tickDuration = 200;
    private int maxArmyAllies = 6;
    private int maxHealthDamage = 10;
    private float flatHealing = 100;
    private float missingHealing = 3.5f; // %

    public UndyingArmy(int maxHealthDamage) {
        this();
        this.maxHealthDamage = maxHealthDamage;
    }

    public UndyingArmy() {
        super("Undying Army", 0, 0, 62.64f, 60);
    }

    @Override
    public void updateDescription(Player player) {
        description = "You may chain up to §e" + maxArmyAllies + " §7allies in a §e" + radius + " §7block radius to heal them for §a" +
                format(flatHealing) + " §7+ §a" + missingHealing + "% §7of their missing health every second. Lasts §6" + format(tickDuration / 20f) + " §7seconds." +
                "\n\nChained allies that take fatal damage will be revived with §a100% §7of their max health and §e100% §7max energy. Revived allies rapidly " +
                "take §c" + maxHealthDamage + "% §7of their max health as damage every second.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Armied", "" + playersArmied));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 2, 0.3f);
        Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 0.9f);

        // particles
        Location loc = player.getEyeLocation();
        loc.setPitch(0);
        loc.setYaw(0);
        Matrix4d matrix = new Matrix4d();
        for (int i = 0; i < 9; i++) {
            loc.setYaw(loc.getYaw() + 360F / 9F);
            matrix.updateFromLocation(loc);
            for (int c = 0; c < 30; c++) {
                double angle = c / 30D * Math.PI * 2;
                double width = 1.5;

                player.getWorld().spawnParticle(
                        Particle.ENCHANTMENT_TABLE,
                        matrix.translateVector(player.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0.1,
                        0,
                        0,
                        null,
                        true
                );
            }

            for (int c = 0; c < 15; c++) {
                double angle = c / 15D * Math.PI * 2;
                double width = 0.6;

                player.getWorld().spawnParticle(
                        Particle.SPELL,
                        matrix.translateVector(player.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0,
                        0,
                        0,
                        null,
                        true
                );
            }
        }

        new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                player.getLocation(),
                radius,
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE).particlesPerCircumference(2)
        ).playEffects();

        UndyingArmy tempUndyingArmy = new UndyingArmy(maxHealthDamage);
        tempUndyingArmy.setPveUpgrade(pveUpgrade);
        int numberOfPlayersWithArmy = 0;
        for (WarlordsEntity teammate : PlayerFilter.entitiesAround(wp, radius, radius, radius)
                                                   .aliveTeammatesOf(wp)
                                                   .closestWarlordPlayersFirst(wp.getLocation())
        ) {
            tempUndyingArmy.getPlayersPopped().put(teammate, false);
            if (teammate != wp) {
                playersArmied++;
                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                        .append(Component.text(" Your ", NamedTextColor.GRAY))
                        .append(Component.text("Undying Army", NamedTextColor.YELLOW))
                        .append(Component.text(" is now protecting " + teammate.getName() + ".", NamedTextColor.GRAY))
                );
                teammate.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                        .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                        .append(Component.text("Undying Army", NamedTextColor.YELLOW))
                        .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                        .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                        .append(Component.text(" seconds.", NamedTextColor.GRAY))
                );
            }
            teammate.getCooldownManager().addRegularCooldown(
                    name,
                    "ARMY",
                    UndyingArmy.class,
                    tempUndyingArmy,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
                            if (!cooldown.getCooldownObject().isArmyDead(teammate)) {
                                float healAmount = flatHealing + (teammate.getMaxHealth() - teammate.getHealth()) * (missingHealing / 100f);
                                teammate.addHealingInstance(wp, name, healAmount, healAmount, 0, 100, false, false);
                                teammate.playSound(teammate.getLocation(), "paladin.holyradiance.activation", 0.1f, 0.7f);
                                // Particles
                                Location playerLoc = teammate.getLocation();
                                playerLoc.add(0, 2.1, 0);
                                Location particleLoc = playerLoc.clone();
                                for (int i = 0; i < 1; i++) {
                                    for (int j = 0; j < 10; j++) {
                                        double angle = j / 10D * Math.PI * 2;
                                        double width = 0.5;
                                        particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                        particleLoc.setY(playerLoc.getY() + i / 5D);
                                        particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                        particleLoc.getWorld().spawnParticle(
                                                Particle.REDSTONE,
                                                particleLoc,
                                                1,
                                                0,
                                                0,
                                                0,
                                                0,
                                                new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1),
                                                true
                                        );
                                    }
                                }
                            }
                        }
                    })
            );

            numberOfPlayersWithArmy++;

            if (numberOfPlayersWithArmy >= maxArmyAllies) {
                break;
            }
        }

        return true;
    }

    public HashMap<WarlordsEntity, Boolean> getPlayersPopped() {
        return playersPopped;
    }

    public boolean isArmyDead(WarlordsEntity warlordsPlayer) {
        return playersPopped.get(warlordsPlayer);
    }

    public void pop(WarlordsEntity warlordsPlayer) {
        playersPopped.put(warlordsPlayer, true);
    }

    public void setMaxArmyAllies(int maxArmyAllies) {
        this.maxArmyAllies = maxArmyAllies;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getMaxHealthDamage() {
        return maxHealthDamage;
    }

    public void setMaxHealthDamage(int maxHealthDamage) {
        this.maxHealthDamage = maxHealthDamage;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public float getFlatHealing() {
        return flatHealing;
    }

    public void setFlatHealing(float flatHealing) {
        this.flatHealing = flatHealing;
    }

    public float getMissingHealing() {
        return missingHealing;
    }

    public void setMissingHealing(float missingHealing) {
        this.missingHealing = missingHealing;
    }
}
