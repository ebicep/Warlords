package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AvengersStrike extends AbstractStrikeBase {
    private boolean pveUpgrade = false;
    protected float energyStole = 0;
    private float energySteal = 10;

    public AvengersStrike() {
        super("Avenger's Strike", 359, 485, 0, 90, 25, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and removing §e" + format(energySteal) + " §7energy.";
        description =
                WordWrap.wrapWithNewline(ChatColor.GRAY +
                                "Strike the targeted enemy player," +
                                "causing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage" +
                                "and removing §e" + format(energySteal) + " §7energy.",
                        DESCRIPTION_WIDTH
                );
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));
        info.add(new Pair<>("Energy Removed", "" + Math.round(energyStole)));

        return info;
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        float multiplier = 1;
        if (nearPlayer instanceof WarlordsNPC) {
            if (pveUpgrade) {
                switch (((WarlordsNPC) nearPlayer).getMobTier()) {
                    case BASE:
                        multiplier = 1.4f;
                        break;
                    case ELITE:
                        multiplier = 1.2f;
                        break;
                }
            }
        }
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
                minDamage.get() * multiplier,
                maxDamage.get() * multiplier,
                critChance,
                critMultiplier,
                false
        );
        energyStole += nearPlayer.subtractEnergy(energySteal, true);
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

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public float getEnergySteal() {
        return energySteal;
    }

    public void setEnergySteal(float energySteal) {
        this.energySteal = energySteal;
    }
}
