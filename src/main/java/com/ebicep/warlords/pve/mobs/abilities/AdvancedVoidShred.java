package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class AdvancedVoidShred extends AbstractAbility {

    private final float voidRadius;
    private final int slowness;
    private final int helixDots;

    public AdvancedVoidShred(float minDamageHeal, float maxDamageHeal, float cooldown, int slowness, float voidRadius, int helixDots) {
        super("Void Shred", minDamageHeal, maxDamageHeal, cooldown, 50);
        this.voidRadius = voidRadius;
        this.slowness = slowness;
        this.helixDots = helixDots;
    }

    @Override
    public void updateDescription(Player player) {

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        EffectUtils.playHelixAnimation(wp.getLocation(), voidRadius, Particle.SMOKE_NORMAL, 1, helixDots);
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(wp, voidRadius, voidRadius, voidRadius)
                .aliveEnemiesOf(wp)
        ) {
            enemy.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
            enemy.addSpeedModifier(wp, "Void Slowness", slowness, 10, "BASE");
        }
        return true;
    }
}
