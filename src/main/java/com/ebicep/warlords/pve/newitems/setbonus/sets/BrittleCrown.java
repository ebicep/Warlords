package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

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
                public void onCurrency(WarlordsAddCurrencyEvent event) {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                        return;
                    }
                    WarlordsDeathEvent killSource = event.getKillSource();
                    if (killSource == null ||
                            !Objects.equals(killSource.getKiller(), warlordsPlayer) ||
                            killSource.getWarlordsEntity().getTeam().equals(warlordsPlayer.getTeam())
                    ) {
                        return;
                    }
                    event.setCurrencyToAdd(event.getCurrencyToAdd() * (1 + insigniaGainBonusPercent / 100f));
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
            ).addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                int loss = (int) Math.floor(warlordsPlayer.getCurrency() * insigniaLossOnHitPercent / 100f);
                warlordsPlayer.setCurrency(Math.max(0, warlordsPlayer.getCurrency() - loss));
                warlordsPlayer.sendMessage(Component.text("You lost " + loss + " ❂ insignia!", NamedTextColor.RED));
            }));
        }

    }

}
