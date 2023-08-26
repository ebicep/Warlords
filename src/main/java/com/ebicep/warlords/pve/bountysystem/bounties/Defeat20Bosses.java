package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounties;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BossMinion;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class Defeat20Bosses extends AbstractBounty implements TracksDuringGame, DailyRewardSpendable1 {

    private static final int TARGET_KILLS = 20;
    private int kills = 0;
    @Transient
    private int newKills = 0;

    @Nullable
    @Override
    public Component getProgress() {
        if (kills >= TARGET_KILLS) {
            return null;
        }
        return getProgress(kills, TARGET_KILLS);
    }

    @Override
    public String getDescription() {
        return "Defeat " + TARGET_KILLS + " bosses in any gamemode.";
    }

    @Override
    public Bounties getBounty() {
        return Bounties.DEFEAT20BOSSES;
    }

    @Override
    public void onFinalDamageHeal(UUID uuid, WarlordsDamageHealingFinalEvent event) {
        if (!event.getAttacker().getUuid().equals(uuid)) {
            return;
        }
        WarlordsEntity victim = event.getWarlordsEntity();
        if (!event.isDead() || !(victim instanceof WarlordsNPC warlordsNPC)) {
            return;
        }
        if (warlordsNPC.getMob() instanceof BossMob && !(warlordsNPC.getMob() instanceof BossMinion)) {
            kills++;
        }
    }

    @Override
    public void apply() {
        kills += newKills;
    }

    @Override
    public void reset() {
        newKills = 0;
    }
}
