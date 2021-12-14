package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;


public class UndyingArmy extends AbstractAbility {

    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(ChatColor.RED + "Instant Kill")
            .lore("§7Right-click this item to die\n§7instantly instead of waiting for\n§7the decay.")
            .get();

    private final int radius = 15;
    private final int duration = 10;
    private final int maxArmyAllies = 6;

    private HashMap<UUID, Boolean> playersPopped = new HashMap<>();

    public HashMap<UUID, Boolean> getPlayersPopped() {
        return playersPopped;
    }

    public boolean isArmyDead(UUID uuid) {
        return playersPopped.get(uuid);
    }

    public void pop(UUID uuid) {
        playersPopped.put(uuid, true);
    }

    public UndyingArmy() {
        super("Undying Army", 0, 0, 62.64f, 60, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You may chain up to §e" + maxArmyAllies + " §7allies in a §e" + radius + "\n" +
                "§7block radius to heal them for §a200 §7+\n" +
                "§7§a6% §7of their missing health every second.\n" +
                "Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Chained allies that take fatal damage\n" +
                "§7will be revived with §a100% §7of their max health\n" +
                "§7and §e100% §7max energy. Revived allies rapidly\n" +
                "§7take §c10% §7of their max health as damage every\n" +
                "§7second.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        UndyingArmy tempUndyingArmy = new UndyingArmy();
        int numberOfPlayersWithArmy = 0;
        for (WarlordsPlayer teammate : PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .closestFirst(wp)
        ) {
            tempUndyingArmy.getPlayersPopped().put(teammate.getUuid(), false);
            if (teammate != wp) {
                wp.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your " + ChatColor.YELLOW + "Undying Army" + ChatColor.GRAY + " is now protecting " + teammate.getName() + ChatColor.GRAY + ".");
                teammate.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + wp.getName() + "'s " + ChatColor.YELLOW + "Undying Army" + ChatColor.GRAY + " is now protecting you for " + ChatColor.GOLD + duration + ChatColor.GRAY + " seconds.");
            }
            teammate.getCooldownManager().addCooldown(name, UndyingArmy.this.getClass(), tempUndyingArmy, "ARMY", duration, wp, CooldownTypes.ABILITY);
            wp.getGame().getGameTasks().put(

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (teammate.getCooldownManager().getCooldown(tempUndyingArmy).isPresent()) {
                                if (!((UndyingArmy) teammate.getCooldownManager().getCooldown(tempUndyingArmy).get().getCooldownObject()).isArmyDead(teammate.getUuid())) {
                                    float healAmount = 200 + (teammate.getMaxHealth() - teammate.getHealth()) / 14.3f;
                                    teammate.healHealth(wp, name, healAmount, healAmount, -1, 100, false);
                                    player.playSound(teammate.getLocation(), "paladin.holyradiance.activation", 0.15f, 0.7f);

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
                    }.runTaskTimer(Warlords.getInstance(), 0, 40),
                    System.currentTimeMillis()
            );
            numberOfPlayersWithArmy++;

            if (numberOfPlayersWithArmy >= maxArmyAllies) {
                break;
            }
        }
        //subtracting to remove self
        numberOfPlayersWithArmy--;
        String allies = numberOfPlayersWithArmy == 1 ? "ally." : "allies.";
        wp.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your " + ChatColor.YELLOW + "Undying Army" + ChatColor.GRAY + " is now protecting " + ChatColor.YELLOW + numberOfPlayersWithArmy + ChatColor.GRAY + " nearby " + allies);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 2, 0.3f);
            player1.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 2, 0.9f);
        }

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
    }
}
