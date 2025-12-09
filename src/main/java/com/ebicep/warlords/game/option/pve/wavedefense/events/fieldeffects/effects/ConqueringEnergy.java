package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ConqueringEnergy implements FieldEffect {
    @Override
    public String getName() {
        return "Conquering Energy";
    }

    @Override
    public String getDescription() {
        return "Reduce EPS by 10, base EPH increased by 150%. Melee damage increased by 50%.";
    }

    @Override
    public void onStart(Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!(event.getSource() instanceof WarlordsPlayer)) {
                    return;
                }
                if (event.getCause().isEmpty()) {
                    event.applyToMinMax(floatModifiable ->
                            floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1.5f)
                    );
                }
            }

        });
    }

    @Override
    public void onWarlordsEntityCreated(WarlordsEntity player) {
        player.getEnergyPerSec().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), -10);
        player.getEnergyPerHit().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, getName(), 1.5f);
    }
}
