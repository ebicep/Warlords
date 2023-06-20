package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.abilties.internal.Shield;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcaneShield extends AbstractAbility implements Duration {

    public int timesBroken = 0;

    private int maxShieldHealth;
    private int shieldPercentage = 50;
    private int tickDuration = 120;
    private float shieldHealth = 0;

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 31.32f, 40);
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
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Broken", "" + timesBroken));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);

        wp.getCooldownManager().addRegularCooldown(
                name,
                "ARCA",
                Shield.class,
                new Shield(name, maxShieldHealth),
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
                    }
                },
                cooldownManager -> {
                },
                tickDuration,
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

        return true;
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
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
