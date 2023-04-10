package com.ebicep.warlords.game.option.pve.wavedefense.events.modes;

import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddVelocityEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.EventNoDrawOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaroShadow;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class BoltaroBonanzaOption implements Option, EventNoDrawOption {

    private final AtomicInteger highestSplitValue = new AtomicInteger();

    @Override
    public void start(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            final EventPointsOption eventPointsOption = game
                    .getOptions()
                    .stream()
                    .filter(option -> option instanceof EventPointsOption)
                    .map(EventPointsOption.class::cast)
                    .findAny()
                    .orElse(null);

            @EventHandler
            public void onMobSpawn(WarlordsMobSpawnEvent event) {
                AbstractMob<?> mob = event.getMob();
                if (mob instanceof EventBoltaroShadow) {
                    int split = ((EventBoltaroShadow) mob).getSplit();
                    if (split > highestSplitValue.get()) {
                        if (eventPointsOption != null) {
                            eventPointsOption.addToAll(500);
                        }
                        highestSplitValue.set(split);
                    }
                }
            }

            @EventHandler
            public void onVelocity(WarlordsAddVelocityEvent event) {
                if (event.getWarlordsEntity() instanceof WarlordsNPC) {
                    Vector vector = event.getVector();
                    vector.setY(vector.getY() * .75);
                }
            }

        });
    }

    @Override
    public void sendEventStatsMessage(@Nonnull Game game, @Nonnull Player player) {
        ChatUtils.sendMessage(player,
                true,
                ChatColor.WHITE + "Highest Split: " + ChatColor.GOLD + highestSplitValue.get()
        );
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            AbilityTree abilityTree = ((WarlordsPlayer) player).getAbilityTree();
            abilityTree.setMaxMasterUpgrades(6);
            for (AbstractUpgradeBranch<?> upgradeBranch : abilityTree.getUpgradeBranches()) {
                upgradeBranch.setMaxUpgrades(8);
            }
        }
    }


    public int getHighestSplitValue() {
        return highestSplitValue.get();
    }
}
