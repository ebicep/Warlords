package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;


public class UndyingArmy extends AbstractAbility {

    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(ChatColor.RED + "Instant Kill")
            .lore("§7Right-click this item to die\n§7instantly instead of waiting for\n§7the decay.")
            .get();

    private final int radius = 15;
    private int duration = 10;
    private int maxArmyAllies = 6;

    private final HashMap<WarlordsPlayer, Boolean> playersPopped = new HashMap<>();

    public HashMap<WarlordsPlayer, Boolean> getPlayersPopped() {
        return playersPopped;
    }

    public boolean isArmyDead(WarlordsPlayer warlordsPlayer) {
        return playersPopped.get(warlordsPlayer);
    }

    public void pop(WarlordsPlayer warlordsPlayer) {
        playersPopped.put(warlordsPlayer, true);
    }

    public UndyingArmy() {
        super("Undying Army", 0, 0, 62.64f, 60, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You may chain up to §e" + maxArmyAllies + " §7allies in a §e" + radius + "\n" +
                "§7block radius to heal them for §a100 §7+\n" +
                "§7§a3.5% §7of their missing health every second.\n" +
                "Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Chained allies that take fatal damage\n" +
                "§7will be revived with §a100% §7of their max health\n" +
                "§7and §e100% §7max energy. Revived allies rapidly\n" +
                "§7take §c10% §7of their max health as damage every\n" +
                "§7second.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        UndyingArmy tempUndyingArmy = new UndyingArmy();
        int numberOfPlayersWithArmy = 0;
        for (WarlordsPlayer teammate : PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .closestFirst(wp)
        ) {
            tempUndyingArmy.getPlayersPopped().put(teammate, false);
            if (teammate != wp) {
                wp.sendMessage(
                    WarlordsPlayer.GIVE_ARROW_GREEN +
                            ChatColor.GRAY + " Your " +
                            ChatColor.YELLOW + "Undying Army" +
                            ChatColor.GRAY + " is now protecting " +
                            teammate.getName() +
                            ChatColor.GRAY + "."
                );

                teammate.sendMessage(
                        WarlordsPlayer.RECEIVE_ARROW_GREEN +
                                ChatColor.GRAY + " " +
                                ChatColor.GRAY + wp.getName() + "'s " +
                                ChatColor.YELLOW + "Undying Army" +
                                ChatColor.GRAY + " is now protecting you for " +
                                ChatColor.GOLD + duration +
                                ChatColor.GRAY + " seconds."
                );
            }
            teammate.getCooldownManager().addRegularCooldown(name, "ARMY", UndyingArmy.class, tempUndyingArmy, wp, CooldownTypes.ABILITY, cooldownManager -> {
            }, duration * 20);
            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    Optional<UndyingArmy> optionalUndyingArmy = new CooldownFilter<>(teammate, RegularCooldown.class).findFirstObject(tempUndyingArmy, UndyingArmy.class);
                    if (optionalUndyingArmy.isPresent()) {
                        if (!(optionalUndyingArmy.get()).isArmyDead(teammate)) {
                            float healAmount = 100 + (teammate.getMaxHealth() - teammate.getHealth()) * 0.035f;
                            teammate.addHealingInstance(wp, name, healAmount, healAmount, -1, 100, false, false);
                            teammate.playSound(teammate.getLocation(), "paladin.holyradiance.activation", 0.1f, 0.7f);

                            // particles
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

                                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 255, 255), particleLoc, 500);
                                }
                            }
                        } else {
                            this.cancel();
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 20);
            numberOfPlayersWithArmy++;

            if (numberOfPlayersWithArmy >= maxArmyAllies) {
                break;
            }
        }

        Utils.playGlobalSound(player.getLocation(), Sound.ZOMBIE_IDLE, 2, 0.3f);
        Utils.playGlobalSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 2, 0.9f);

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

                ParticleEffect.ENCHANTMENT_TABLE.display(0, 0.1f, 0, 0, 1,
                        matrix.translateVector(player.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width), 500);
            }

            for (int c = 0; c < 15; c++) {
                double angle = c / 15D * Math.PI * 2;
                double width = 0.6;

                ParticleEffect.SPELL.display(0, 0, 0, 0, 1,
                        matrix.translateVector(player.getWorld(), radius, Math.sin(angle) * width, Math.cos(angle) * width), 500);
            }
        }

        CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), radius);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE).particlesPerCircumference(2));
        circle.playEffects();

        return true;
    }

    public void setMaxArmyAllies(int maxArmyAllies) {
        this.maxArmyAllies = maxArmyAllies;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
