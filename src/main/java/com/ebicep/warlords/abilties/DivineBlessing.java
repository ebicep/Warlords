package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class DivineBlessing extends AbstractAbility implements Duration {

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
        Utils.playGlobalSound(player.getLocation(), "arcanist.divineblessing.activation", 2, 1.35f);
        Utils.playGlobalSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1.5f);

        wp.getCooldownManager().removeCooldown(DivineBlessing.class, false);
        List<BeaconOfImpair> effectedBeacons = new CooldownFilter<>(wp, RegularCooldown.class)
                .filterCooldownFrom(wp)
                .filterCooldownClassAndMapToObjectsOfClass(BeaconOfImpair.class)
                .collect(Collectors.toList());
        effectedBeacons.forEach(beaconAbility -> beaconAbility.setRadius(beaconAbility.getRadius() + beaconImpairRangeIncrease));

        DivineBlessing tempDivineBlessing = new DivineBlessing();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "BLESS",
                DivineBlessing.class,
                tempDivineBlessing,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    effectedBeacons.forEach(beaconAbility -> beaconAbility.setRadius(beaconAbility.getRadius() - beaconImpairRangeIncrease));
                },
                tickDuration
        ) {
            @Override
            public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * (1 + healingReceivedIncrease / 100f);
            }

            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cd = event.getAbstractCooldown();
                        if (event.getWarlordsEntity().equals(wp) && cd.getCooldownObject() instanceof BeaconOfImpair beacon) {
                            beacon.setRadius(beacon.getRadius() + beaconImpairRangeIncrease);
                            effectedBeacons.add(beacon);
                        }
                    }
                };
            }
        });

        for (WarlordsEntity teammate : PlayerFilter.playingGame(wp.getGame()).teammatesOfExcludingSelf(wp)) {
            teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "BLESS",
                    DivineBlessing.class,
                    tempDivineBlessing,
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
