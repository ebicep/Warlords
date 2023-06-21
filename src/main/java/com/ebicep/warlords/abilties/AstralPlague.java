package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;

public class AstralPlague extends AbstractAbility implements Duration {

    private int tickDuration = 240;
    private int hexTickDurationIncrease = 40;

    public AstralPlague() {
        super("Astral Plague", 0, 0, 50, 10, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Grant yourself Astral Energy, increasing Poisonous Hex duration by ")
                               .append(Component.text(format(hexTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and causing Soulfire Beam to not consume Poisonous Hex stacks. " +
                                       "Your attacks pierces shields and defenses of enemies with "))
                               .append(Component.text("3", NamedTextColor.RED))
                               .append(Component.text(" stacks of Poisonous Hex. Lasts"))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. "));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ASTRAL",
                AstralPlague.class,
                new AstralPlague(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (cooldown.getFrom().equals(wp) &&
                                cooldown instanceof RegularCooldown<?> regularCooldown &&
                                cooldown.getCooldownObject() instanceof PoisonousHex
                        ) {
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                        }
                    }
                };
            }
        });
        PlayerFilter.playingGame(wp.getGame())
                    .enemiesOf(wp)
                    .forEach(enemy -> {
                        new CooldownFilter<>(enemy, RegularCooldown.class)
                                .filterCooldownClass(PoisonousHex.class)
                                .filterCooldownFrom(wp)
                                .forEach(cd -> cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease));
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
