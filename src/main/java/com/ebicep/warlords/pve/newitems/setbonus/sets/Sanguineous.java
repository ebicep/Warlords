package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.ScrollOfScripts;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Sanguineous extends BaseSet {

    private int damagePerHealthyPlayerPercent;
    private int healthyPlayerThresholdPercent;

    @Override
    public void init() {
        super.init();
        this.damagePerHealthyPlayerPercent = getValue("damagePerHealthyPlayerPercent", int.class);
        this.healthyPlayerThresholdPercent = getValue("healthyPlayerThresholdPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sanguineous";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damagePerHealthyPlayerPercent, healthyPlayerThresholdPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Sanguineous.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {

                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + 0.05f * numberOfPlayersAbove75(warlordsPlayer)
                        );
                    })
            );
        }

        public static int numberOfPlayersAbove75(WarlordsPlayer warlordsPlayer) {
            return warlordsPlayer.getGame()
                    .warlordsPlayers()
                    .filter(player -> player.getCurrentHealth() > player.getMaxHealth() * .75)
                    .mapToInt(player -> 1)
                    .sum();
        }
    }
}