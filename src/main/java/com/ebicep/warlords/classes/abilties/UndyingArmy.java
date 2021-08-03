package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
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

    private final int radius = 15;

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

                        for (Player player1 : wp.getWorld().getPlayers()) {
                            player1.playSound(wp.getLocation(), "paladin.holyradiance.activation", 0.5f, 1);
                        }
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

                            for (Player player1 : teammate.getWorld().getPlayers()) {
                                player1.playSound(teammate.getLocation(), "paladin.holyradiance.activation", 0.5f, 1);
                            }
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
            player1.playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 1, 1.1f);
        }

        CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), 6);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY).particlesPerCircumference(1));
        circle.playEffects();
    }
}
