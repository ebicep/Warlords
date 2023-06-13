package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class AstralPlague extends AbstractAbility implements Duration {

    private int tickDuration = 240;

    public AstralPlague() {
        super("Astral Plague", 0, 0, 50, 10, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Grant yourself Astral Energy, increasing Poisonous Hex duration to ")
                               .append(Component.text("6", NamedTextColor.GOLD))
                               .append(Component.text(
                                       " seconds and causing Soulfire Beam to not consume Poisonous Hex stacks. Your attacks pierces shields and defenses of enemies with "))
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
        ));
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
