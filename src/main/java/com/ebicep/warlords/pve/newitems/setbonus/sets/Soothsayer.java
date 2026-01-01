package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbilityStats;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Soothsayer extends BaseSet {

    private int runeChance;
    private int runeCount;

    @Override
    public void init() {
        super.init();
        this.runeChance = getValue("runeChance", int.class);
        this.runeCount = getValue("runeCount", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "soothsayer";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(runeChance, runeCount);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {
                @EventHandler
                public void onAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
                    if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                        return;
                    }
                    AbstractPlayerClass playerSpec = warlordsPlayer.getSpec();
                    if (!Objects.equals(event.getAbility(), playerSpec.getWeapon())) {
                        return;
                    }
                    if (ThreadLocalRandom.current().nextDouble() > runeChance / 100.0) {
                        return;
                    }
                    List<AbstractAbility> abilities = playerSpec.getAbilitiesExcludingWeapon();
                    //picking random ability
                    AbstractAbility ability = abilities.get(ThreadLocalRandom.current().nextInt(abilities.size()));
                    ability.onActivate(warlordsPlayer);
                    if (ability instanceof AbilityStats<?, ?> abilityStats) {
                        abilityStats.getAbilityStats().addTimesUsed();
                    }
                    AbstractPlayerClass.sendRightClickPacket(warlordsPlayer);
                }
            });
        }

    }

}