package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUseEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Energize extends BaseSet {

    private int energyPerKillThreshold;
    private int energyGained;
    private int freeAbilityCastChancePercent;

    @Override
    public void init() {
        super.init();
        this.energyPerKillThreshold = getValue("energyPerKillThreshold", int.class);
        this.energyGained = getValue("energyGained", int.class);
        this.freeAbilityCastChancePercent = getValue("freeAbilityCastChancePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "energize";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyPerKillThreshold, energyGained, freeAbilityCastChancePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        private int kills = 0;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            Listener listener = new Listener() {
                @EventHandler
                public void onEnemyDeath(WarlordsDeathEvent event) {
                    WarlordsEntity entity = event.getWarlordsEntity();
                    if (entity.equals(warlordsPlayer)) {
                        return;
                    }
                    if (entity.getTeam().equals(warlordsPlayer.getTeam())) {
                        return;
                    }

                    kills++;

                    if (kills == 5) {
                        kills = 0;
                        warlordsPlayer.addEnergy(warlordsPlayer, getName(), energyGained);
                    }
                }

                @EventHandler
                public void onEnergySpent(WarlordsEnergyUseEvent.Pre event) {
                    if (ThreadLocalRandom.current().nextDouble() > freeAbilityCastChancePercent / 100.0) {
                        return;
                    }
                    warlordsPlayer.sendMessage(Component.text("Your energize gave you a free ability cast!", NamedTextColor.GREEN));
                    event.setCancelled(true);
                }
            };
            warlordsPlayer.getGame().registerEvents(listener);
        }

    }

}