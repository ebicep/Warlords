package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.abilties.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ContagiousFacade extends AbstractAbility implements Duration {

    private int damageAbsorption = 30;
    private int tickDuration = 100;
    private int shieldTickDuration = 100;
    private int speedIncrease = 40;
    private int speedIncreaseDuration = 100;

    public ContagiousFacade() {
        super("Contagious Facade", 0, 0, 30, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Cover yourself in a protective layer that absorbs ")
                               .append(Component.text(damageAbsorption + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of all incoming damage for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. "))
                               .append(Component.text("\n\nReactivate the ability to grant yourself a shield equal to all the damage you have absorbed during " + name + ". Lasts "))
                               .append(Component.text(format(shieldTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. \n\nNot reactivating the ability will instead increase your speed by "))
                               .append(Component.text(speedIncrease + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(format(speedIncreaseDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        AtomicDouble totalAbsorbed = new AtomicDouble(0);
        RegularCooldown<ContagiousFacade> protectiveLayerCooldown = new RegularCooldown<>(
                name,
                "FACADE",
                ContagiousFacade.class,
                new ContagiousFacade(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    wp.getSpeed().addSpeedModifier(wp, name, speedIncrease, speedIncreaseDuration, "BASE");
                },
                tickDuration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterValue = currentDamageValue * (100 - damageAbsorption) / 100f;
                totalAbsorbed.addAndGet(currentDamageValue - afterValue);
                return afterValue;
            }
        };
        wp.getCooldownManager().addCooldown(protectiveLayerCooldown);
        addSecondaryAbility(() -> {
                    if (!wp.isAlive()) {
                        return;
                    }
                    wp.getCooldownManager().removeCooldownNoForce(protectiveLayerCooldown);

                    // copied from arcane shield, easiest but not technically an "arcane shield"
                    Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);
                    wp.getCooldownManager().addRegularCooldown(
                            name,
                            "SHIELD",
                            Shield.class,
                            new Shield(name, (int) totalAbsorbed.get()),
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            cooldownManager -> {
                            },
                            shieldTickDuration,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksElapsed % 3 == 0) {
                                    Location location = wp.getLocation();
                                    location.add(0, 1.5, 0);
                                    World world = location.getWorld();
                                    world.spawnParticle(Particle.CLOUD, location, 2, 0.15F, 0.3F, 0.15F, 0.01, null, true);
                                    world.spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0.3F, 0.3F, 0.3F, 0.0001, null, true);
                                    world.spawnParticle(Particle.SPELL_WITCH, location, 1, 0.3F, 0.3F, 0.3F, 0, null, true);
                                }
                            })
                    );
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(protectiveLayerCooldown)
        );
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

    public int getDamageAbsorption() {
        return damageAbsorption;
    }

    public void setDamageAbsorption(int damageAbsorption) {
        this.damageAbsorption = damageAbsorption;
    }
}