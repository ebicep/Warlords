package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CrusadersStrike extends AbstractStrikeBase {
    protected int energyGivenToPlayers = 0;
    private boolean pveUpgrade = false;
    private int energyGiven = 24;
    private int energyRadius = 10;
    private int energyMaxAllies = 2;

    public CrusadersStrike() {
        super("Crusader's Strike", 326, 441, 0, 90, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage and\n" +
                "§7restoring §e" + energyGiven + " §7energy to " + energyMaxAllies + " nearby\n" +
                "§7allies within §e" + energyRadius + " §7blocks." +
                "\n\n" +
                "§7MARKED allies get priority in restoring energy and\n" +
                "§7increases their speed by §e40% §7for §61 §7second.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));
        info.add(new Pair<>("Energy Given", "" + energyGivenToPlayers));

        return info;
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        AtomicReference<Float> minDamage = new AtomicReference<>(minDamageHeal);
        AtomicReference<Float> maxDamage = new AtomicReference<>(maxDamageHeal);
        getStandingOnConsecrate(wp, nearPlayer).ifPresent(consecrate -> {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            minDamage.getAndUpdate(value -> value *= (1 + consecrate.getStrikeDamageBoost() / 100f));
            maxDamage.getAndUpdate(value -> value *= (1 + consecrate.getStrikeDamageBoost() / 100f));
        });
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                minDamage.get(),
                maxDamage.get(),
                critChance,
                critMultiplier,
                false
        );

        if (pveUpgrade) {
            tripleHit(wp, nearPlayer);
        }

        int previousEnergyGiven = energyGivenToPlayers;
        // Give energy to nearby allies and check if they have mark active
        for (WarlordsEntity energyTarget : PlayerFilter
                .entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .sorted(Comparator.comparing((WarlordsEntity p) -> p.getCooldownManager().hasCooldown(HolyRadianceCrusader.class) ? 0 : 1)
                        .thenComparing(Utils.sortClosestBy(WarlordsEntity::getLocation, wp.getLocation()))
                )
                .limit(energyMaxAllies)
        ) {
            if (energyTarget.getCooldownManager().hasCooldown(HolyRadianceCrusader.class)) {
                energyTarget.getSpeed().addSpeedModifier("CRUSADER MARK", 40, 20, "BASE"); // 20 ticks
            }

            energyGivenToPlayers += energyTarget.addEnergy(wp, name, energyGiven);
        }

        new CooldownFilter<>(wp, RegularCooldown.class)
                .filter(regularCooldown -> regularCooldown.getFrom().equals(wp))
                .filterCooldownClassAndMapToObjectsOfClass(InspiringPresence.class)
                .forEach(inspiringPresence -> inspiringPresence.addEnergyGivenFromStrikeAndPresence(energyGivenToPlayers - previousEnergyGiven));

        return true;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        ParticleEffect.SPELL.display(
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1,
                4,
                location.clone().add(0, 1, 0),
                500);
    }

    public int getEnergyGiven() {
        return energyGiven;
    }

    public void setEnergyGiven(int energyGiven) {
        this.energyGiven = energyGiven;
    }

    public int getEnergyRadius() {
        return energyRadius;
    }

    public void setEnergyRadius(int energyRadius) {
        this.energyRadius = energyRadius;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getEnergyMaxAllies() {
        return energyMaxAllies;
    }

    public void setEnergyMaxAllies(int energyMaxAllies) {
        this.energyMaxAllies = energyMaxAllies;
    }
}
