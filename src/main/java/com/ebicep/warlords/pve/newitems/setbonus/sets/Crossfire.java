package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Crossfire extends BaseSet {

    private int allyRadius;
    private int energyRegenIncreasePercent;
    private int damageIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.allyRadius = getValue("allyRadius", int.class);
        this.energyRegenIncreasePercent = getValue("energyRegenIncreasePercent", int.class);
        this.damageIncreasePercent = getValue("damageIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "crossfire";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyRegenIncreasePercent, damageIncreasePercent);
    }

    private boolean isIsolated(WarlordsPlayer warlordsPlayer) {
        if (warlordsPlayer.isDead()) {
            return false;
        }
        double radiusSquared = allyRadius * allyRadius;
        return warlordsPlayer.getGame()
                .warlordsPlayers()
                .filter(ally -> ally != warlordsPlayer)
                .filter(warlordsPlayer::isTeammateAlive)
                .noneMatch(ally -> ally.getLocation().distanceSquared(warlordsPlayer.getLocation()) <= radiusSquared);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Crossfire.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.ENERGY_GAIN_PER_TICK,
                    energyGainPerTick -> {
                        if (!isIsolated(warlordsPlayer)) {
                            return;
                        }
                        energyGainPerTick.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + energyRegenIncreasePercent / 100f
                        );
                    }
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        if (!isIsolated(warlordsPlayer)) {
                            return;
                        }
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + damageIncreasePercent / 100f
                        );
                    }
            ));
        }

    }

}
