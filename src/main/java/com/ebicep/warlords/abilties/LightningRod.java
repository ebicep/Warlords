package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractTotem;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LightningRod extends AbstractAbility {

    private final int knockbackRadius = 5;
    private int energyRestore = 160;
    private int healthRestore = 30;

    public LightningRod() {
        super("Lightning Rod", 0, 0, 31.32f, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Call down an energizing bolt of lightning upon yourself, restoring ")
                               .append(Component.text(healthRestore + "%", NamedTextColor.GREEN))
                               .append(Component.text(" health and "))
                               .append(Component.text(energyRestore + " ", NamedTextColor.YELLOW))
                               .append(Component.text("energy and knock all nearby enemies in a "))
                               .append(Component.text(knockbackRadius + " ", NamedTextColor.YELLOW))
                               .append(Component.text("block radius back."));

    }


    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.addEnergy(wp, name, energyRestore);
        Utils.playGlobalSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);

        new FallingBlockWaveEffect(wp.getLocation(), knockbackRadius, 1, Material.ORANGE_TULIP).play();
        player.getWorld().spigot().strikeLightningEffect(wp.getLocation(), true);

        wp.addHealingInstance(
                wp,
                name,
                (wp.getMaxHealth() * (healthRestore / 100f)),
                (wp.getMaxHealth() * (healthRestore / 100f)),
                critChance,
                critMultiplier,
                false,
                false
        );

        for (WarlordsEntity knockbackTarget : PlayerFilter
                .entitiesAround(player, knockbackRadius, knockbackRadius, knockbackRadius)
                .aliveEnemiesOf(wp)
        ) {
            final Location loc = knockbackTarget.getLocation();
            final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.35);
            knockbackTarget.setVelocity(name, v, false);
        }

        // pulsedamage
        List<CapacitorTotem> totemDownAndClose = AbstractTotem.getTotemsDownAndClose(wp, wp.getEntity(), CapacitorTotem.class);
        totemDownAndClose.forEach(capacitorTotem -> {
            ArmorStand totem = capacitorTotem.getTotem();

            Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
            player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);

            capacitorTotem.pulseDamage();
            capacitorTotem.pulseDamage();
            if (capacitorTotem.isPveMasterUpgrade()) {
                capacitorTotem.setRadius(capacitorTotem.getRadius() + 0.5);
            }
            capacitorTotem.addProc();
        });

        if (pveMasterUpgrade) {
            damageIncreaseOnUse(wp);
        }

        return true;
    }

    private void damageIncreaseOnUse(WarlordsEntity we) {
        we.getSpeed().addSpeedModifier(we, "Rod Speed", 40, 12 * 20, "BASE");
        we.getCooldownManager().removeCooldown(LightningRod.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ROD DMG",
                LightningRod.class,
                new LightningRod(),
                we,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                12 * 20
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.4f;
            }
        });
    }

    public int getHealthRestore() {
        return healthRestore;
    }

    public void setHealthRestore(int healthRestore) {
        this.healthRestore = healthRestore;
    }

    public int getEnergyRestore() {
        return energyRestore;
    }

    public void setEnergyRestore(int energyRestore) {
        this.energyRestore = energyRestore;
    }


}
