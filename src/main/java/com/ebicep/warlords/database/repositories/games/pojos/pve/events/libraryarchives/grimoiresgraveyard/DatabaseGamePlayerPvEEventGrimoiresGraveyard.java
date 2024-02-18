package com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.GrimoiresGraveyardOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodex;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseGamePlayerPvEEventGrimoiresGraveyard extends DatabaseGamePlayerPvEEvent {

    @Field("codex_earned")
    private PlayerCodex codexEarned;

    public DatabaseGamePlayerPvEEventGrimoiresGraveyard() {
    }

    public DatabaseGamePlayerPvEEventGrimoiresGraveyard(
            WarlordsPlayer warlordsPlayer,
            WarlordsGameTriggerWinEvent gameWinEvent,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption,
            GrimoiresGraveyardOption grimoiresGraveyardOption,
            boolean counted
    ) {
        super(warlordsPlayer, gameWinEvent, waveDefenseOption, eventPointsOption, counted);
        this.codexEarned = grimoiresGraveyardOption.getCodexRewards().get(warlordsPlayer.getUuid());
    }

    public PlayerCodex getCodexEarned() {
        return codexEarned;
    }
}
