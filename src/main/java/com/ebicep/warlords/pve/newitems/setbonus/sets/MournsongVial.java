package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class MournsongVial extends BaseSet {

    private int allyEnergyPerSecondBonusPercent;
    private int selfDamageTakenIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.allyEnergyPerSecondBonusPercent = getValue("allyEnergyPerSecondBonusPercent", int.class);
        this.selfDamageTakenIncreasePercent = getValue("selfDamageTakenIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "mournsongVial";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(allyEnergyPerSecondBonusPercent, selfDamageTakenIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            PlayerFilter.playingGame(warlordsPlayer.getGame())
                    .excluding(warlordsPlayer)
                    .forEach(player -> {
                        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                                warlordsPlayer.getName() + "'s" + getName(),
                                null,
                                MournsongVial.class,
                                null,
                                warlordsPlayer,
                                CooldownTypes.ITEM,
                                cooldownManager -> {
                                },
                                false
                        ).addModifier(
                                Modifier.ENERGY_GAIN_PER_TICK,
                                (energy) -> {
                                    energy.addModifier(
                                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                            getName(),
                                            1 + (allyEnergyPerSecondBonusPercent / 100f)
                                    );
                                }
                        ));
            });
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    MournsongVial.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + (selfDamageTakenIncreasePercent / 100f)
                        );
                    }
            ));
        }

    }

}