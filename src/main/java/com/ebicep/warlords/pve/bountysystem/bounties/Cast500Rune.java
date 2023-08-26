package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounties;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class Cast500Rune extends AbstractBounty implements TracksDuringGame, DailyRewardSpendable1 {

    private static final int TARGET = 500;
    private int used = 0;
    @Transient
    private int newUsed = 0;

    @Nullable
    @Override
    public Component getProgress() {
        if (used >= TARGET) {
            return null;
        }
        return getProgress(used, TARGET);
    }

    @Override
    public String getDescription() {
        return "Cast " + TARGET + " rune abilities in any gamemode.";
    }

    @Override
    public Bounties getBounty() {
        return Bounties.CAST500RUNE;
    }

    @Override
    public void onAbilityUsed(UUID uuid, WarlordsAbilityActivateEvent event) {
        if (!event.getWarlordsEntity().getUuid().equals(uuid)) {
            return;
        }
        newUsed++;
    }

    @Override
    public void apply() {
        used += newUsed;
    }

    @Override
    public void reset() {
        newUsed = 0;
    }
}
