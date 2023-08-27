package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.java.Pair;

import java.util.HashMap;
import java.util.Map;

public class Challenge4 extends AbstractBounty implements TracksPostGame, WeeklyRewardSpendable3 {

    private Map<DifficultyIndex, Integer> difficultiesBeat = new HashMap<>() {{
        put(DifficultyIndex.EASY, 0);
        put(DifficultyIndex.NORMAL, 0);
        put(DifficultyIndex.HARD, 0);
        put(DifficultyIndex.EXTREME, 0);
    }};

    @Override
    public String getName() {
        return "Challenge";
    }

    @Override
    public String getDescription() {
        return "Complete Easy, Normal, Hard, and Extreme Mode with guildmates.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CHALLENGE4;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        BountyUtils.getPvEOptionFromGame(game, WaveDefenseOption.class).ifPresent(waveDefenseOption -> {
            DifficultyIndex difficulty = waveDefenseOption.getDifficulty();
            if (waveDefenseOption.getWavesCleared() != difficulty.getMaxWaves()) {
                return;
            }
            if (!difficultiesBeat.containsKey(difficulty)) {
                return;
            }
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(warlordsPlayer.getUuid());
            if (guildPlayerPair == null) {
                return;
            }
            Guild guild = guildPlayerPair.getA();
            if (!game.warlordsPlayers()
                     .allMatch(wp -> {
                         Pair<Guild, GuildPlayer> pair = GuildManager.getGuildAndGuildPlayerFromPlayer(warlordsPlayer.getUuid());
                         return pair != null && pair.getA().equals(guild);
                     })
            ) {
                return;
            }
            difficultiesBeat.merge(difficulty, 1, Integer::sum);
            if (difficultiesBeat.values().stream().allMatch(integer -> integer >= 1)) {
                value++;
            }
        });
    }

}
