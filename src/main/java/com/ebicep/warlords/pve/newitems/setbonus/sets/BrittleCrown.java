package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class BrittleCrown extends BaseSet {

    private int insigniaGainBonusPercent;
    private int insigniaLossOnHitPercent;

    @Override
    public void init() {
        super.init();
        this.insigniaGainBonusPercent = getValue("insigniaGainBonusPercent", int.class);
        this.insigniaLossOnHitPercent = getValue("insigniaLossOnHitPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "brittleCrown";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(insigniaGainBonusPercent, insigniaLossOnHitPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {
                @EventHandler
                public void onInsigniaGain(WarlordsAddCurrencyEvent event) {
                    float currency = event.getCurrencyToAdd();
                    event.setCurrencyToAdd(currency * (1 + insigniaGainBonusPercent / 100f));
                }
            });
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    BrittleCrown.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.ON_INCOMING_DAMAGE,
                    (event, currentDamageValue, isCrit) -> {
                        int loss = (int) Math.floor(warlordsPlayer.getCurrency() * insigniaLossOnHitPercent / 100f);
                        int newValue = (int) Math.max(0L, warlordsPlayer.getCurrency() - loss);
                        warlordsPlayer.setCurrency(newValue);
                        warlordsPlayer.sendMessage(Component.text("You lost " + loss + " ❂ insignia!", NamedTextColor.RED));
                    }
            ));
        }

    }

}