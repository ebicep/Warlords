package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Soulbinding extends AbstractAbility {

    private List<SoulBoundPlayer> soulBindedPlayers = new ArrayList<>();
    private int bindDuration = 2;
    private final int duration = 12;

    public Soulbinding() {
        super("Soulbinding Weapon", 0, 0, 21.92f, 30, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Your melee attacks §dBIND\n" +
                "§7enemies for §6" + bindDuration + " §7seconds.\n" +
                "§7Against §dBOUND §7targets, your\n" +
                "§7next Spirit Link will heal you for\n" +
                "§a400 §7health (half for §e2 §7nearby allies.)\n" +
                "§7Your next Fallen Souls will reduce the\n" +
                "§7cooldown of all abilities by §61.5\n" +
                "§7seconds. (§61 §7second for §e2 §7nearby\n" +
                "§7allies). Both buffs may be activated for\n" +
                "§7every melee hit. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Successful soulbind procs will grant you\n" +
                "§7§625% §7knockback resistance for §61.2\n" +
                "§7seconds. (max §63.6 §7seconds)";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addCooldown(name, Soulbinding.this.getClass(), new Soulbinding(), "SOUL", duration, wp, CooldownTypes.ABILITY);

        ItemMeta newItemMeta = player.getInventory().getItem(0).getItemMeta();
        newItemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
        player.getInventory().getItem(0).setItemMeta(newItemMeta);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 2);
        }
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!wp.getCooldownManager().getCooldown(Soulbinding.class).isEmpty()) {
                            Location location = player.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.SPELL_WITCH.display(0.2F, 0F, 0.2F, 0.1F, 1, location, 500);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 4),
                System.currentTimeMillis()
        );
    }

    public List<SoulBoundPlayer> getSoulBindedPlayers() {
        return soulBindedPlayers;
    }

    public boolean hasBoundPlayer(WarlordsPlayer warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBoundPlayerSoul(WarlordsPlayer warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithSoul()) {
                    soulBindedPlayer.setHitWithSoul(true);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public boolean hasBoundPlayerLink(WarlordsPlayer warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithLink()) {
                    soulBindedPlayer.setHitWithLink(true);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public int getBindDuration() {
        return bindDuration;
    }

    public void setBindDuration(int bindDuration) {
        this.bindDuration = bindDuration;
    }

    public static class SoulBoundPlayer {
        private WarlordsPlayer boundPlayer;
        private float timeLeft;
        private boolean hitWithLink;
        private boolean hitWithSoul;

        public SoulBoundPlayer(WarlordsPlayer boundPlayer, int timeLeft) {
            this.boundPlayer = boundPlayer;
            this.timeLeft = timeLeft;
            hitWithLink = false;
            hitWithSoul = false;
        }

        public WarlordsPlayer getBoundPlayer() {
            return boundPlayer;
        }

        public void setBoundPlayer(WarlordsPlayer boundPlayer) {
            this.boundPlayer = boundPlayer;
        }

        public float getTimeLeft() {
            return timeLeft;
        }

        public void decrementTimeLeft() {
            this.timeLeft -= .5;
        }

        public void setTimeLeft(float timeLeft) {
            this.timeLeft = timeLeft;
        }

        public boolean isHitWithLink() {
            return hitWithLink;
        }

        public void setHitWithLink(boolean hitWithLink) {
            this.hitWithLink = hitWithLink;
        }

        public boolean isHitWithSoul() {
            return hitWithSoul;
        }

        public void setHitWithSoul(boolean hitWithSoul) {
            this.hitWithSoul = hitWithSoul;
        }
    }
}
