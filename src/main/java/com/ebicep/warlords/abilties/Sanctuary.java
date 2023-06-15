package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
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

public class Sanctuary extends AbstractAbility implements Duration {

    private int damageReflected = 25;
    private int hexShieldIncrease = 200;
    private int hexTickDurationIncrease = 20;
    private int allyHexStackGain = 1;
    private int tickDuration = 160;

    public Sanctuary() {
        super("Sanctuary", 0, 0, 52, 40, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Summon your full protective power, infusing your soul with innate resilience. All incoming attacks reflect ")
                               .append(Component.text(damageReflected + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of their damage back to the dealer. All Fortifying Hexes are improved, gaining "))
                               .append(Component.text(hexShieldIncrease, NamedTextColor.YELLOW))
                               .append(Component.text(" health and "))
                               .append(Component.text(format(hexTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" second duration. Additionally, the nearest ally receives "))
                               .append(Component.text(allyHexStackGain, NamedTextColor.BLUE))
                               .append(Component.text(" stack of Fortifying Hex whenever you obtain "))
                               .append(Component.text("1", NamedTextColor.BLUE))
                               .append(Component.text(" stack. Lasts "))
                               .append(Component.text(format(hexTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
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
                "SANCTUARY",
                Sanctuary.class,
                new Sanctuary(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration
        ) {

            private final float damageTakenMultiplier = 1 - damageReflected / 100f;
            private final float damageReflectedMultiplier = damageReflected / 100f;

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                event.getAttacker().addDamageInstance(
                        event.getWarlordsEntity(),
                        name,
                        currentDamageValue * damageReflectedMultiplier,
                        currentDamageValue * damageReflectedMultiplier,
                        0,
                        100,
                        false
                );
                return currentDamageValue * damageTakenMultiplier;
            }

            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (cooldown.getFrom().equals(wp) &&
                                cooldown instanceof RegularCooldown<?> regularCooldown &&
                                cooldown.getCooldownObject() instanceof FortifyingHex.FortifyingHexShield shield
                        ) {
                            shield.setMaxShieldHealth(shield.getMaxShieldHealth() + hexShieldIncrease);
                            shield.setShieldHealth(shield.getShieldHealth() + hexShieldIncrease);
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                            if (event.getWarlordsEntity().equals(wp)) {
                                PlayerFilter.playingGame(wp.getGame()).teammatesOfExcludingSelf(wp)
                                            .closestFirst(wp)
                                            .limit(1)
                                            .forEach(warlordsEntity -> {
                                                warlordsEntity.getCooldownManager().limitCooldowns(
                                                        RegularCooldown.class,
                                                        FortifyingHex.FortifyingHexShield.class,
                                                        shield.getMaxStacks()
                                                );
                                                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                        name,
                                                        "FHEX",
                                                        FortifyingHex.FortifyingHexShield.class,
                                                        new FortifyingHex.FortifyingHexShield(name, shield.getMaxShieldHealth(), shield.getMaxStacks()),
                                                        wp,
                                                        CooldownTypes.ABILITY,
                                                        cooldownManager -> {
                                                        },
                                                        regularCooldown.getTicksLeft()
                                                ));
                                            });
                            }
                        }
                    }
                };
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

    public int getDamageReflected() {
        return damageReflected;
    }

    public void setDamageReflected(int damageReflected) {
        this.damageReflected = damageReflected;
    }
}
