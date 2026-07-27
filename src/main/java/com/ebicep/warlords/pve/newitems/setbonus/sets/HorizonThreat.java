package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class HorizonThreat extends BaseSet {

    private int farDistance;
    private int farDamageIncreasePercent;
    private int nearDistance;
    private int nearDamageTakenIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.farDistance = getValue("farDistance", int.class);
        this.farDamageIncreasePercent = getValue("farDamageIncreasePercent", int.class);
        this.nearDistance = getValue("nearDistance", int.class);
        this.nearDamageTakenIncreasePercent = getValue("nearDamageTakenIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "horizonThreat";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(farDistance, farDamageIncreasePercent, nearDistance, nearDamageTakenIncreasePercent);
    }

    private boolean isWithinDistance(WarlordsEntity first, WarlordsEntity second, int distance) {
        return first.getWorld() == second.getWorld() &&
                first.getLocation().distanceSquared(second.getLocation()) < distance * distance;
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    HorizonThreat.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                WarlordsEntity target = event.getWarlordsEntity();
                if (warlordsPlayer.getWorld() != target.getWorld() ||
                        warlordsPlayer.getLocation().distanceSquared(target.getLocation()) <= farDistance * farDistance
                ) {
                    return;
                }
                currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getName(),
                        1 + farDamageIncreasePercent / 100f
                );
            }).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                WarlordsEntity source = event.getSource();
                if (source == warlordsPlayer || !isWithinDistance(warlordsPlayer, source, nearDistance)) {
                    return;
                }
                currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getName(),
                        1 + nearDamageTakenIncreasePercent / 100f
                );
            }));
        }

    }

}
