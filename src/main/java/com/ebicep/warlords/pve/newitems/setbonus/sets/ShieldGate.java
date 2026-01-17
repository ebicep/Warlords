package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class ShieldGate extends BaseSet {

    private int shieldEffectivenessIncreasePercent;
    private int maxHealthReductionPercent;

    @Override
    public void init() {
        super.init();
        this.shieldEffectivenessIncreasePercent = getValue("shieldEffectivenessIncreasePercent", int.class);
        this.maxHealthReductionPercent = getValue("maxHealthReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "shieldGate";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(shieldEffectivenessIncreasePercent, maxHealthReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 - maxHealthReductionPercent / 100f);
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    ShieldGate.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {

                    },
                    false
            ));
            Listener listener = new Listener() {
                @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
                public void onAddCooldown(WarlordsAddCooldownEvent event) {
                    AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                    if (!(cooldown.getCooldownObject() instanceof Shield shield)) {
                        return;
                    }

                    boolean isIncomingShield = event.getWarlordsEntity() == warlordsPlayer;
                    boolean isOutgoingShield = cooldown.getFrom() == warlordsPlayer;

                    if (!isIncomingShield && !isOutgoingShield) {
                        return;
                    }

                    shield.setMaxShieldHealth(shield.getMaxShieldHealth() * shieldEffectivenessIncreasePercent);
                    shield.setShieldHealth(shield.getShieldHealth() * shieldEffectivenessIncreasePercent);
                }
            };
            warlordsPlayer.getGame().registerEvents(listener);

            // Implementation for:
            // 1. Reducing the player's Max Health by 99% (forcing reliance on shields).
            // 2. Applying a 3.0x (300%) multiplier to all incoming/outgoing shield amounts.
        }

    }

}