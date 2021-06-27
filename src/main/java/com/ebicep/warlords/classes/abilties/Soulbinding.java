package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Soulbinding extends AbstractAbility {

    public Soulbinding() {
        super("Soulbinding Weapon", 0, 0, 21.92f, 30, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Your melee attacks §dBIND\n" +
                "§7enemies for §62 §7seconds.\n" +
                "§7Against §dBOUND §7targets, your\n" +
                "§7next Spirit Link will heal you and\n" +
                "§e2 §7nearby allies for §a420 §7health.\n" +
                "§7Your next Fallen Souls will reduce the\n" +
                "§7cooldown of all abilities by §61.5\n" +
                "§7seconds. (§60.5 §7seconds for §e2 §7nearby\n" +
                "§7allies). Both buffs may be activated for\n" +
                "§7every melee hit. Lasts §612 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.getCooldownManager().addCooldown(Soulbinding.this.getClass(), "SOUL", 12, warlordsPlayer, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 2);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (warlordsPlayer.getCooldownManager().getCooldown(Soulbinding.class).size() > 0) {
                    Location location = player.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.SPELL_WITCH.display(0.2F, 0F, 0.2F, 0.1F, 1, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 4);
    }

    public static class SoulBoundPlayer {
        private WarlordsPlayer boundPlayer;
        private int timeLeft;
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

        public int getTimeLeft() {
            return timeLeft;
        }

        public void setTimeLeft(int timeLeft) {
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
