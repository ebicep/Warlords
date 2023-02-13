package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcaneShield extends AbstractAbility {

    public int timesBroken = 0;

    private int maxShieldHealth;
    private int shieldPercentage = 50;
    private int duration = 6;
    private float shieldHealth = 0;

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 31.32f, 40);
    }

    public ArcaneShield(int shieldHealth) {
        this();
        this.shieldHealth = shieldHealth;
    }

    @Override
    public void updateDescription(Player player) {
        description = "Surround yourself with arcane energy, creating a shield that will absorb up to §e" + maxShieldHealth +
                " §7(§e" + shieldPercentage + "% §7of your maximum health) incoming damage. Lasts §6" + duration + " §7seconds.";
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
        ArcaneShield tempArcaneShield = new ArcaneShield(maxShieldHealth);

        wp.getCooldownManager().addRegularCooldown(
                name,
                "ARCA",
                ArcaneShield.class,
                tempArcaneShield,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveUpgrade) {
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
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(ArcaneShield.class).stream().count() == 1) {
                        if (wp.getEntity() instanceof Player) {
                            ((LivingEntity) ((CraftPlayer) wp.getEntity()).getHandle()).setAbsorptionAmount(0);
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

        if (player != null) {
            ((LivingEntity) ((CraftPlayer) player).getHandle()).setAbsorptionAmount(20);
        }

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
