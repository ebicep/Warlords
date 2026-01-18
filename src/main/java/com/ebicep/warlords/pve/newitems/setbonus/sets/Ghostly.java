package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Ghostly extends BaseSet {

    private int dodgeChance;

    @Override
    public void init() {
        super.init();
        this.dodgeChance = getValue("dodgeChance", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ghostly";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(dodgeChance);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {
                @EventHandler
                public void onDamageHeal(WarlordsDamageHealingEvent event) {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                        return;
                    }
                    if (event.isHealingInstance()) {
                        return;
                    }
                    if (ThreadLocalRandom.current().nextDouble() < dodgeChance / 100.0) {
                        warlordsPlayer.sendMessage(Component.text("Your " + getName() + " dodged ", NamedTextColor.GREEN)
                                .append(event.getSource().getColoredName())
                                .append(Component.text("'s attack.")));
                        event.setCancelled(true);
                    }
                }
            });
            // Implementation for:
            // 1. Roll for dodgeChance when taking damage.
            // 2. On successful dodge, apply/increment stacking damage and healing buffs.
        }

    }

}