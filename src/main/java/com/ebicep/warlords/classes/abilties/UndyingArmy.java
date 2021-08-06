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


public class UndyingArmy extends AbstractAbility {

    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(ChatColor.RED + "Instant Kill")
            .lore("§7Right-click this item to die\n§7instantly instead of waiting for\n§7the decay.")
            .get();

    private final int radius = 12;

    private boolean armyDead = false;

    //dead = true - take 10% dmg
    //dead = false - heal
    public boolean isArmyDead() {
        return this.armyDead;
    }

    public void pop() {
        this.armyDead = true;
    }

    public UndyingArmy() {
        super("Undying Army", 0, 0, 65, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7When you or nearby allies take\n" +
                "§7fatal damage within §610 §7seconds,\n" +
                "§7instantly restore them to §a100% §7health\n" +
                "§7instead. They will take §c500 §7TRUE DAMAGE\n" +
                "§7every second for the rest of their life.\n" +
                "§7Allies not revived will heal for §a200 §7+\n" +
                "§a35% §7of their missing health §610 §7seconds\n" +
                "§7after this abilty was cast.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        UndyingArmy tempUndyingArmy = new UndyingArmy();
        wp.getCooldownManager().addCooldown(UndyingArmy.this.getClass(), tempUndyingArmy, "ARMY", 10, wp, CooldownTypes.ABILITY);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (wp.getCooldownManager().getCooldown(tempUndyingArmy).isPresent()) {
                    if (!((UndyingArmy) wp.getCooldownManager().getCooldown(tempUndyingArmy).get().getCooldownObject()).isArmyDead()) {
                        float healAmount = 100 + (wp.getMaxHealth() - wp.getHealth()) / 10f;
                        wp.addHealth(wp, name, healAmount, healAmount, -1, 100);
                        player.playSound(wp.getLocation(), "paladin.holyradiance.activation", 0.35f, 0.75f);
                    } else {
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 40);

        int numberOfPlayersWithArmy = 0;
        for (WarlordsPlayer teammate : PlayerFilter.entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            wp.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is now protecting " + teammate.getColoredName() + ChatColor.GRAY + ".");
            teammate.getCooldownManager().addCooldown(UndyingArmy.this.getClass(), tempUndyingArmy, "ARMY", 10, wp, CooldownTypes.ABILITY);
            teammate.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + wp.getName() + "'s Undying Army protects you for " + ChatColor.GOLD + "10 " + ChatColor.GRAY + "seconds.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (teammate.getCooldownManager().getCooldown(tempUndyingArmy).isPresent()) {
                        if (!((UndyingArmy) teammate.getCooldownManager().getCooldown(tempUndyingArmy).get().getCooldownObject()).isArmyDead()) {
                            float healAmount = 100 + (teammate.getMaxHealth() - teammate.getHealth()) / 10f;
                            teammate.addHealth(wp, name, healAmount, healAmount, -1, 100);
                            player.playSound(teammate.getLocation(), "paladin.holyradiance.activation", 0.35f, 0.75f);
                        } else {
                            this.cancel();
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 0, 40);

            numberOfPlayersWithArmy++;

            if (numberOfPlayersWithArmy >= 5) {
                break;
            }
        }
        String allies = numberOfPlayersWithArmy == 1 ? "ally." : "allies.";
        wp.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is now protecting " + ChatColor.YELLOW + numberOfPlayersWithArmy + ChatColor.GRAY + " nearby " + allies);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 2, 0.3f);
        }

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
        circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY).particlesPerCircumference(2));
        circle.playEffects();
    }
}
