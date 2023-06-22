package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.OrbPassenger;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class CrystalOfHealing extends AbstractAbility {

    private static final float RADIUS = 1.5f;
    private int duration = 20; // seconds
    private int maxHeal = 1000;
    private int lifeSpan = 40; // seconds

    public CrystalOfHealing() {
        super("Crystal of Healing", 0, 0, 20, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Create a crystal of healing that absorbs surrounding light over ")
                               .append(Component.text(format(duration), NamedTextColor.GOLD))
                               .append(Component.text(" seconds, gradually increasing the amount of health it will restore to one ally when they absorb it, to a maximum of "))
                               .append(Component.text(maxHeal, NamedTextColor.GREEN))
                               .append(Component.text(" health. The crystal of healing has a lifespan of "))
                               .append(Component.text(format(lifeSpan), NamedTextColor.GOLD))
                               .append(Component.text(" seconds after its completion."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);

        Location groundLocation = LocationUtils.getGroundLocation(player);

        Crystal crystal = new Crystal(groundLocation, wp);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "CRYSTAL",
                CrystalOfHealing.class,
                new CrystalOfHealing(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    crystal.remove();
                },
                false,
                (duration + lifeSpan) * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        crystal.getArmorStand().customName(Component.text(ticksLeft / 20, NamedTextColor.GREEN));
                        //crystal.getTextDisplay().text(Component.text(ticksLeft / 20, NamedTextColor.GOLD));
                    }
                    if (ticksElapsed < 40) {
                        return; // prevent instant pickup
                    }
                    PlayerFilter.entitiesAround(groundLocation, RADIUS, RADIUS, RADIUS)
                                .teammatesOf(wp)
                                .closestFirst(groundLocation)
                                .first(teammate -> {
                                    cooldown.setTicksLeft(0);
                                    int secondsElapsed = ticksElapsed / 20;
                                    float healAmount = secondsElapsed > duration ? maxHeal : (float) (maxHeal * ticksElapsed) / (duration * 20);
                                    teammate.addHealingInstance(wp, name, healAmount, healAmount, critChance, critMultiplier, false, false);
                                });
                })
        ));

        return true;
    }

    static class Crystal extends OrbPassenger {

        private TextDisplay textDisplay;

        public Crystal(Location location, WarlordsEntity owner) {
            super(location, owner, 1, stand -> {
                stand.setCustomNameVisible(true);
            });
//            textDisplay = location.getWorld().spawn(location.clone().add(0, 2.25, 0), TextDisplay.class, display -> {
//                //display.text();
//            });
        }

        @Override
        public void remove() {
            super.remove();
//            textDisplay.remove();
        }

        public TextDisplay getTextDisplay() {
            return textDisplay;
        }
    }
}
