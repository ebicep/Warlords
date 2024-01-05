package com.ebicep.warlords.game.option.pve.wavedefense.events.modes;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.ExperienceGainOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects.CodexCollector;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.chat.ChatUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class ForgottenCodexOption implements Option {

    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        int codexesEquipped = 0;
        for (Option option : game.getOptions()) {
            if (!(option instanceof FieldEffectOption fieldEffectOption)) {
                continue;
            }
            for (FieldEffect fieldEffect : fieldEffectOption.getFieldEffects()) {
                if (!(fieldEffect instanceof CodexCollector codexCollector)) {
                    continue;
                }
                codexesEquipped = codexCollector.getCodexesEquipped();
            }
        }
        ChatUtils.MessageType.GAME_EVENTS.sendMessage("ForgottenCodexOption: codexesEquipped = " + codexesEquipped);
        if (codexesEquipped < 6) {
            return;
        }
        for (Option option : game.getOptions()) {
            if (option instanceof CoinGainOption gainOption) {
                gainOption.playerCoinPerXSec(150, 5);
                gainOption.guildCoinPerXSec(1, 1);
            } else if (option instanceof ExperienceGainOption experienceGainOption) {
                experienceGainOption.playerExpPerXSec(10, 5);
                experienceGainOption.guildExpPerXSec(30, 30);
            }
        }
    }
}
