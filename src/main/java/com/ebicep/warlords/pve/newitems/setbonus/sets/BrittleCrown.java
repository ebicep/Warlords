package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
            AtomicBoolean killWindow = new AtomicBoolean(false);
            warlordsPlayer.getGame().registerEvents(new Listener() {

                @EventHandler
                public void onDeath(WarlordsDeathEvent event) {
                    if (!Objects.equals(event.getKiller(), warlordsPlayer) ||
                            event.getWarlordsEntity().getTeam().equals(warlordsPlayer.getTeam())
                    ) {
                        return;
                    }
                    killWindow.set(true);
                    new GameRunnable(warlordsPlayer.getGame()) {

                        @Override
                        public void run() {
                            killWindow.set(false);
                        }

                    }.runTaskLater(5);
                }

                @EventHandler
                public void onCurrency(WarlordsAddCurrencyEvent event) {
                    if (event.getWarlordsEntity().equals(warlordsPlayer) && killWindow.get()) {
                        event.setCurrencyToAdd(event.getCurrencyToAdd() * (1 + insigniaGainBonusPercent / 100f));
                    }
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
