package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class SpiritualShield extends AbstractAbility implements Duration {

    private int tickDuration = 120;
    private float runeTickIncrease = 0.5f;

    public SpiritualShield() {
        super("Spiritual Shield", 0, 0, 30, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Surround yourself with spirits for ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. While active, increase the attacker’s rune timers by "))
                               .append(Component.text(format(runeTickIncrease), NamedTextColor.GOLD))
                               .append(Component.text(" seconds for every instance of damage they deal to you."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SPIRITUAL",
                SpiritualShield.class,
                new SpiritualShield(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getAttacker().getSpec().increaseAllCooldownTimersBy(runeTickIncrease);
            }
        });
        return true;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
