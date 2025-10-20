package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractArcaneShield;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.ArcaneShieldBranchAquamancer;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Collections;

public class ArcaneShieldAquamancer extends AbstractArcaneShield {

    public ArcaneShieldAquamancer() {
        super(AbstractAbilityBuilder.create("arcaneShieldAquamancer").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);
        Shield shield = new Shield(name, getMaxShieldHealth());
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ARCA",
                Shield.class,
                shield,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (shield.isBroken()) {
                        Bukkit.getPluginManager().callEvent(new WarlordsArcaneShieldBrokenEvent(wp));
                        getAbilityStats().timesBroken++;
                    }
                    getAbilityStats().totalAbsorbed += shield.getMaxShieldHealth() - Math.max(0, shield.getShieldHealth());
                },
                getTickDuration(),
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.CLOUD, location, 2, 0.15, 0.3, 0.15, 0.01);
                        EffectUtils.displayParticle(Particle.FIREWORK, location, 1, 0.3, 0.3, 0.3, 0.0001);
                        EffectUtils.displayParticle(Particle.WITCH, location, 1, 0.3, 0.3, 0.3, 0);
                    }
                })
        ) {
            @Override
            public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getWarlordsEntity().getCooldownManager().queueUpdatePlayerNames();
            }

            @Override
            public PlayerNameData addPrefixFromOther() {
                return new PlayerNameData(Component.text((int) (shield.getShieldHealth()), NamedTextColor.YELLOW), we -> we.isTeammate(wp));
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ArcaneShieldBranchAquamancer(abilityTree, this);
    }
}
