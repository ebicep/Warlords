package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WarriorsTriumph implements FieldEffect {
    @Override
    public String getName() {
        return "Warrior's Triumph";
    }

    @Override
    public String getDescription() {
        return "Ability durations are reduced by 30% on ability activation for non-Warrior specializations. Warrior strikes deal 200% more damage.";
    }

    @Override
    public void onStart(Game game) {
        game.registerEvents(new Listener() {
            @EventHandler
            public void onCooldown(WarlordsAddCooldownEvent event) {
                if (!(event.getWarlordsEntity() instanceof WarlordsPlayer)) {
                    return;
                }
                if (Specializations.getClass(event.getWarlordsEntity().getSpecClass()) == Classes.WARRIOR) {
                    return;
                }
                AbstractCooldown<?> abstractCooldown = event.getAbstractCooldown();
                if (abstractCooldown instanceof LinkedCooldown) {
                    if (abstractCooldown.getFrom().equals(event.getWarlordsEntity())) {
                        LinkedCooldown<?> linkedCooldown = (LinkedCooldown<?>) abstractCooldown;
                        linkedCooldown.setTicksLeft((int) (linkedCooldown.getTicksLeft() * 0.7));
                    }
                } else if (abstractCooldown instanceof RegularCooldown<?> regularCooldown) {
                    regularCooldown.setTicksLeft((int) (regularCooldown.getTicksLeft() * 0.7));
                }
            }

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!(event.getAttacker() instanceof WarlordsPlayer)) {
                    return;
                }
                if (Specializations.getClass(event.getAttacker().getSpecClass()) != Classes.WARRIOR) {
                    return;
                }
                String ability = event.getAbility();
                if (ability.equals("Wounding Strike") || ability.equals("Crippling Strike")) {
                    event.setMin(event.getMin() * 3);
                    event.setMax(event.getMax() * 3);
                }
            }
        });
    }

}
