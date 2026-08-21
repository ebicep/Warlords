package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;

import java.util.List;

public class Regenerate extends BaseSet {

    @Override
    public String getConfigFieldName() {
        return "regenerate";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of();
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            new GameRunnable(warlordsPlayer.getGame()) {

                private int ticks;

                @Override
                public void run() {
                    if (warlordsPlayer.isDead()) {
                        return;
                    }
                    warlordsPlayer.setRegenTickTimer(1);
                    ticks++;
                    if (ticks % 40 != 0 || warlordsPlayer.getCurrentHealth() >= warlordsPlayer.getMaxHealth()) {
                        return;
                    }
                    int regen = ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "regenHealth", int.class);
                    warlordsPlayer.getRegenPerSecond().setBaseValue(regen);
                    warlordsPlayer.getRegenPerSecond().refresh();
                    warlordsPlayer.setCurrentHealth(Math.min(
                            warlordsPlayer.getMaxHealth(),
                            warlordsPlayer.getCurrentHealth() + warlordsPlayer.getRegenPerSecond().getCalculatedValue()
                    ));
                }

            }.runTaskTimer(0, 1);
        }

    }

}
