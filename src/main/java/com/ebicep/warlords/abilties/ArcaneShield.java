package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcaneShield extends AbstractAbility {
    private boolean pveUpgrade = false;
    private int timesBroken = 0;

    private int duration = 6;
    public int maxShieldHealth;
    public int shieldPercentage = 50;
    private float shieldHealth = 0;

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 31.32f, 40, 0, 0);
    }

    public ArcaneShield(int shieldHealth) {
        super("Arcane Shield", 0, 0, 31.32f, 40, 0, 0);
        this.shieldHealth = shieldHealth;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Surround yourself with arcane\n" +
                "§7energy, creating a shield that will\n" +
                "§7absorb up to §e" + maxShieldHealth + " §7(§e" + shieldPercentage + "% §7of your maximum\n" +
                "§7health) incoming damage. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Broken", "" + timesBroken));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);
        ArcaneShield tempArcaneShield = new ArcaneShield(maxShieldHealth);

        wp.getCooldownManager().addRegularCooldown(
                name,
                "ARCA",
                ArcaneShield.class,
                tempArcaneShield,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(ArcaneShield.class).stream().count() == 1) {
                        if (wp.getEntity() instanceof Player) {
                            ((EntityLiving) ((CraftPlayer) wp.getEntity()).getHandle()).setAbsorptionHearts(0);
                        }
                    }

                    if (pveUpgrade) {
                        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 0.5f);
                        EffectUtils.strikeLightning(wp.getLocation(), false);
                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(wp, 6, 6, 6)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                        ) {
                            we.getSpeed().addSpeedModifier("Arcane Aegis", -99, 6 * 20);
                        }
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        ParticleEffect.CLOUD.display(0.15F, 0.3F, 0.15F, 0.01F, 2, location, 500);
                        ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.3F, 0.3F, 0.0001F, 1, location, 500);
                        ParticleEffect.SPELL_WITCH.display(0.3F, 0.3F, 0.3F, 0, 1, location, 500);
                    }
                })
        );
        ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(20);

        return true;
    }

    public void updateShieldHealth(AbstractPlayerClass apc) {
        if (apc != null) {
            ArcaneShield arcaneShield = (this);
            arcaneShield.setMaxShieldHealth((int) (apc.getMaxHealth() * (arcaneShield.getShieldPercentage() / 100f)));
        }
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

    public void setMaxShieldHealth(int maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
    }

    public int getShieldPercentage() {
        return shieldPercentage;
    }

    public void setShieldPercentage(int shieldPercentage) {
        this.shieldPercentage = shieldPercentage;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
