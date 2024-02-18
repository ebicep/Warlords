package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.ArcaneShieldBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcaneShield extends AbstractAbility implements BlueAbilityIcon, Duration {

    public int timesBroken = 0;

    private int maxShieldHealth;
    private int shieldPercentage = 50;
    private int tickDuration = 120;
    private float shieldHealth = 0;

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 31.32f, 40);
    }

    public void addTimesBroken() {
        timesBroken++;
    }

    public int getTimesBroken() {
        return timesBroken;
    }

    public float getShieldHealth() {
        return shieldHealth;
    }

    public void addShieldHealth(float amount) {
        this.shieldHealth += amount;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Surround yourself with arcane energy, creating a shield that will absorb up to ")
                               .append(Component.text(maxShieldHealth, NamedTextColor.YELLOW))
                               .append(Component.text(" ("))
                               .append(Component.text(shieldPercentage + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of your maximum health) incoming damage. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }


    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Broken", "" + timesBroken));

        return info;
    }


    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);

        Shield shield = new Shield(name, maxShieldHealth);
        wp.getCooldownManager().addRegularCooldown(
                name,
                "ARCA",
                Shield.class,
                shield,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 0.5f);
                        EffectUtils.strikeLightning(wp.getLocation(), false);
                        for (WarlordsNPC we : PlayerFilterGeneric
                                .entitiesAround(wp, 6, 6, 6)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                                .warlordsNPCs()
                        ) {
                            we.setStunTicks(6 * 20);
                        }
                    } else if (pveMasterUpgrade2) {
                        List<AbstractAbility> abilities = wp.getAbilities();
                        if (abilities.size() == 0) {
                            return;
                        }
                        AbstractAbility rightClick = abilities.get(0);
                        FloatModifiable.FloatModifier modifier = rightClick.getEnergyCost().addMultiplicativeModifierAdd("Arcane Energy", -.15f);
                        wp.updateItem(rightClick);
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Arcane Energy",
                                "ARC",
                                ArcaneShield.class,
                                new ArcaneShield(),
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager2 -> {
                                    modifier.forceEnd();
                                    wp.updateItem(rightClick);
                                },
                                100,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed % 3 == 0) {
                                        EffectUtils.displayParticle(
                                                Particle.ELECTRIC_SPARK,
                                                wp.getLocation().add(0, 1, 0),
                                                10,
                                                .4,
                                                .4,
                                                .4,
                                                0
                                        );
                                    }
                                })
                        ));
                    }
                },
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.CLOUD, location, 2, 0.15, 0.3, 0.15, 0.01);
                        EffectUtils.displayParticle(Particle.FIREWORKS_SPARK, location, 1, 0.3, 0.3, 0.3, 0.0001);
                        EffectUtils.displayParticle(Particle.SPELL_WITCH, location, 1, 0.3, 0.3, 0.3, 0);
                    }
                })
        );

        return true;
    }


    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ArcaneShieldBranch(abilityTree, this);
    }


    @Override
    public void updateCustomStats(AbstractPlayerClass apc) {
        if (apc != null) {
            ArcaneShield arcaneShield = (this);
            arcaneShield.setMaxShieldHealth((int) (apc.getMaxHealth() * (arcaneShield.getShieldPercentage() / 100f)));
            updateDescription(null);
        }
    }


    public void setMaxShieldHealth(int maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
    }

    public int getShieldPercentage() {
        return shieldPercentage;
    }

    public void setShieldPercentage(int shieldPercentage) {
        this.shieldPercentage = shieldPercentage;
    }


}
