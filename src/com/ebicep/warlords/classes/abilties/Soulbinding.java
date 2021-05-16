package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;

public class Soulbinding extends AbstractAbility {

    public Soulbinding() {
        super("Soulbinding Weapon", 0, 0, 23, 30, -1, 100, "soulbinding weapon description");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setSoulBindCooldown(12);
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
