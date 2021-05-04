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

        public SoulBoundPlayer(WarlordsPlayer boundPlayer, int timeLeft) {
            this.boundPlayer = boundPlayer;
            this.timeLeft = timeLeft;
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
    }
}
