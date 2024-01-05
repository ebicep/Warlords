package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects.CodexCollector;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodex;

import java.util.HashSet;
import java.util.Set;

public class CodexContinuedI extends AbstractBounty implements TracksPostGame, EventCost, LibraryArchives2 {

    @Override
    public String getName() {
        return "Codex Continued";
    }

    @Override
    public String getDescription() {
        return "Complete Forgotten Codex " + getTarget() + " times with each player having a different Codex equipped.";
    }

    @Override
    public int getTarget() {
        return 10;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CODEX_CONTINUED_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (BountyUtils.lostGame(gameWinEvent)) {
            return;
        }
        BountyUtils.getOptionFromGame(game, FieldEffectOption.class).ifPresent(option -> {
            for (FieldEffect fieldEffect : option.getFieldEffects()) {
                if (!(fieldEffect instanceof CodexCollector codexCollector)) {
                    continue;
                }
                Set<PlayerCodex> codexCount = new HashSet<>();
                codexCollector.getPlayerCodexEquipped().forEach((warlordsEntity, playerCodex) -> codexCount.add(playerCodex));
                if (codexCount.size() == game.warlordsPlayers().count()) {
                    value++;
                }
                return;

            }
        });
    }

}
