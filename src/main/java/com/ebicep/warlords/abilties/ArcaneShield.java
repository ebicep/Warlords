package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ArcaneShield extends AbstractAbility {
    private boolean pveUpgrade = false;
    private float pveDamageReduction;
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
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);
        ArcaneShield tempArcaneShield = new ArcaneShield(maxShieldHealth);

        if (pveUpgrade) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<ArcaneShield>(
                     name,
                     "ARCA RES",
                     ArcaneShield.class,
                     tempArcaneShield,
                     wp,
                     CooldownTypes.ABILITY,
                     cooldownManager -> {
                     },
                     duration * 20
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * getPveDamageReduction();
                }
            });
            Bukkit.broadcastMessage("damage res: " + getPveDamageReduction());
        }

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
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 3 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        ParticleEffect.CLOUD.display(0.15F, 0.3F, 0.15F, 0.01F, 2, location, 500);
                        ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.3F, 0.3F, 0.0001F, 1, location, 500);
                        ParticleEffect.SPELL_WITCH.display(0.3F, 0.3F, 0.3F, 0, 1, location, 500);
                    }
                }
        );
        ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(20);

        return true;
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

    public float getPveDamageReduction() {
        return (100 - pveDamageReduction) / 100f;
    }

    public void setPveDamageReduction(int pveDamageReduction) {
        this.pveDamageReduction = pveDamageReduction;
    }
}
