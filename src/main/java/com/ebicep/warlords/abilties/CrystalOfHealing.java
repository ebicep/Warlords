package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.OrbPassenger;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class CrystalOfHealing extends AbstractAbility {

    private static final float RADIUS = 1.5f;
    private int duration = 20; // seconds
    private float maxHeal = 1200f;
    private int lifeSpan = 40; // seconds

    public CrystalOfHealing() {
        super("Crystal of Healing", 0, 0, 20, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Create a crystal of healing that absorbs surrounding light over ")
                               .append(Component.text(format(duration), NamedTextColor.GOLD))
                               .append(Component.text(" seconds, gradually increasing the amount of health it will restore to one ally when they absorb it, to a maximum of "))
                               .append(Component.text(format(maxHeal), NamedTextColor.GREEN))
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

        Utils.playGlobalSound(wp.getLocation(), "arcanist.crystalofhealing.activation", 2, 0.85f);

        Crystal crystal = new Crystal(groundLocation, wp);
        CircleEffect teamCircleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                crystal.getArmorStand().getLocation(),
                RADIUS,
                new CircumferenceEffect(Particle.WAX_OFF, Particle.REDSTONE)
        );

        FireWorkEffectPlayer.playFirework(crystal.getArmorStand().getLocation(), FireworkEffect.builder()
                .withColor(Color.LIME)
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .build());

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
                    teamCircleEffect.playEffects();
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
                                    teammate.playSound(teammate.getLocation(), "shaman.earthlivingweapon.impact", 1, 0.45f);
                                    FireWorkEffectPlayer.playFirework(groundLocation, FireworkEffect.builder()
                                            .withColor(Color.WHITE)
                                            .with(FireworkEffect.Type.STAR)
                                            .build());
                                    cooldown.setTicksLeft(0);
                                    int secondsElapsed = ticksElapsed / 20;
                                    float healAmount = secondsElapsed > duration ? maxHeal : (float) (maxHeal * ticksElapsed) / (duration * 20);
                                    teammate.addHealingInstance(wp, name, healAmount, healAmount, critChance, critMultiplier, false, false);
                                });
                })
        ));

        return true;
    }

    public float getMaxHeal() {
        return maxHeal;
    }

    public void setMaxHeal(float maxHeal) {
        this.maxHeal = maxHeal;
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
