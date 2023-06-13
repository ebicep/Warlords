package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class DivineBlessing extends AbstractAbility {

    private int tickDuration = 80;
    private int healingReceivedIncrease = 50; // %
    private int beaconLightHealingIncrease = 50; // %
    private int beaconImpairRangeIncrease = 2;
    private int beaconTickDurationIncrease = 80;

    public DivineBlessing() {
        super("Divine Blessing", 0, 0, 38, 50, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Grant a divine blessing to all allies for ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds, increasing the healing received by "))
                               .append(Component.text(healingReceivedIncrease + "%", NamedTextColor.GREEN))
                               .append(Component.text(". Beacon of Light restores "))
                               .append(Component.text(beaconLightHealingIncrease + "%", NamedTextColor.GREEN))
                               .append(Component.text(" more health and Beacon of Impair’s range is increased by "))
                               .append(Component.text(beaconImpairRangeIncrease, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks for "))
                               .append(Component.text(format(beaconTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        for (WarlordsEntity teammate : PlayerFilter.playingGame(wp.getGame())
                                                   .aliveTeammatesOf(wp)
        ) {
            teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "BLESS",
                    DivineBlessing.class,
                    new DivineBlessing(),
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    tickDuration
            ) {
                @Override
                public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                    return currentHealValue * (1 + healingReceivedIncrease / 100f);
                }
            });
        }
        //TODO BEACON
        return true;
    }
}
