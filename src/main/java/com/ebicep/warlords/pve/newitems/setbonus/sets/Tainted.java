package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tainted extends BaseSet {

    private int transferPercent;
    private int maxPerAllyPercent;
    private int durationSeconds;

    @Override
    public void init() {
        super.init();
        this.transferPercent = getValue("transferPercent", int.class);
        this.maxPerAllyPercent = getValue("maxPerAllyPercent", int.class);
        this.durationSeconds = getValue("durationSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "tainted";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(transferPercent, maxPerAllyPercent, (float) durationSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        private final Map<WarlordsEntity, Float> transferredTo = new HashMap<>();
        private float totalTransferred = 0;
        private int stackCounter = 0;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Tainted.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.ON_OUTGOING_HEALING,
                    (event, currentHealValue, isCrit) -> {
                        if (event.getFlags().contains(InstanceFlags.RECURSIVE) || currentHealValue <= 0) {
                            return;
                        }
                        WarlordsEntity healed = event.getWarlordsEntity();
                        if (!(healed instanceof WarlordsPlayer) || healed.equals(warlordsPlayer) || !healed.isTeammate(warlordsPlayer)) {
                            return;
                        }
                        transfer(warlordsPlayer, healed);
                    }
            ));
        }

        private void transfer(WarlordsPlayer warlordsPlayer, WarlordsEntity ally) {
            float untransferredMaxHealth = warlordsPlayer.getMaxHealth() + totalTransferred;
            float alreadyTransferred = transferredTo.getOrDefault(ally, 0f);
            float amount = Math.min(
                    untransferredMaxHealth * transferPercent / 100f,
                    untransferredMaxHealth * maxPerAllyPercent / 100f - alreadyTransferred
            );
            // max health must stay positive so the transfer can never kill the wearer
            amount = Math.min(amount, warlordsPlayer.getMaxHealth() - 1);
            if (amount <= 0) {
                return;
            }
            String log = getName() + " " + warlordsPlayer.getUuid() + " " + stackCounter++;
            ally.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, log, amount);
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, log, -amount);
            transferredTo.merge(ally, amount, Float::sum);
            totalTransferred += amount;

            float finalAmount = amount;
            new GameRunnable(warlordsPlayer.getGame()) {
                @Override
                public void run() {
                    ally.getHealth().removeModifier(log);
                    warlordsPlayer.getHealth().removeModifier(log);
                    transferredTo.computeIfPresent(ally, (entity, total) -> {
                        float remaining = total - finalAmount;
                        return remaining <= 0.001f ? null : remaining;
                    });
                    totalTransferred = Math.max(0, totalTransferred - finalAmount);
                }
            }.runTaskLater(durationSeconds * 20L);
        }

    }

}
