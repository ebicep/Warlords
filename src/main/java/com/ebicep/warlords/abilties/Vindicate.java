package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Vindicate extends AbstractAbility {
    private boolean pveUpgrade = false;
    protected int debuffsRemovedOnCast = 0;

    private final int radius = 8;
    private int vindicateDuration = 12;
    private int vindicateSelfDuration = 8;
    private float vindicateDamageReduction = 30;

    public Vindicate() {
        super("Vindicate", 0, 0, 55, 25, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7All allies within an §e" + radius + " §7block radius gain the\n" +
                "§7status §6VIND§7, which clears all de-buffs. In\n" +
                "§7addition, the status §6VIND §7prevents allies from being\n" +
                "§7affected by de-buffs and grants §650% §7knockback\n" +
                "§7resistance for §6" + vindicateDuration + " §7seconds. You gain §e" + format(vindicateDamageReduction) + "%\n" +
                "§7damage reduction for §6" + vindicateSelfDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Debuffs Removed On Cast", "" + debuffsRemovedOnCast));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "rogue.vindicate.activation", 2, 0.7f);
        Utils.playGlobalSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);

        new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                player.getLocation(),
                radius,
                new CircumferenceEffect(ParticleEffect.SPELL, ParticleEffect.REDSTONE).particlesPerCircumference(2)
        ).playEffects();

        EffectUtils.playHelixAnimation(player, radius, 230, 130, 5);

        Vindicate tempVindicate = new Vindicate();

        if (pveUpgrade) {
            buffOnUse(wp);
        }

        for (WarlordsEntity vindicateTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .closestFirst(wp)
        ) {
            if (vindicateTarget != wp) {
                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                        ChatColor.GRAY + " Your Vindicate is now protecting " +
                        ChatColor.YELLOW + vindicateTarget.getName() +
                        ChatColor.GRAY + "!"
                );

                vindicateTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN + " " +
                        ChatColor.GRAY + wp.getName() + "'s" +
                        ChatColor.YELLOW + " Vindicate" +
                        ChatColor.GRAY + " is now protecting you from de-buffs for " +
                        ChatColor.GOLD + vindicateDuration +
                        ChatColor.GRAY + " seconds!"
                );
            }

            // Vindicate Immunity
            vindicateTarget.getSpeed().removeSlownessModifiers();
            debuffsRemovedOnCast += vindicateTarget.getCooldownManager().removeDebuffCooldowns();
            vindicateTarget.getCooldownManager().removeCooldownByName("Vindicate Debuff Immunity");
            vindicateTarget.getCooldownManager().addRegularCooldown(
                    "Vindicate Debuff Immunity",
                    "VIND",
                    Vindicate.class,
                    tempVindicate,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    vindicateDuration * 20
            );
        }

        wp.getCooldownManager().addCooldown(new RegularCooldown<Vindicate>(
                "Vindicate Resistance",
                "VIND RESIST",
                Vindicate.class,
                tempVindicate,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                vindicateSelfDuration * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * getVindicateDamageReduction();
            }
        });

        return true;
    }

    private void buffOnUse(WarlordsEntity we) {
        we.getSpeed().addSpeedModifier("Vindicate Speed", 25, vindicateDuration);
        // TODO: Add EPS increase
    }

    public float getVindicateDamageReduction() {
        return (100 - vindicateDamageReduction) / 100f;
    }

    public void setVindicateDamageReduction(float vindicateDamageReduction) {
        this.vindicateDamageReduction = vindicateDamageReduction;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getVindicateDuration() {
        return vindicateDuration;
    }

    public void setVindicateDuration(int vindicateDuration) {
        this.vindicateDuration = vindicateDuration;
    }

    public int getVindicateSelfDuration() {
        return vindicateSelfDuration;
    }

    public void setVindicateSelfDuration(int vindicateSelfDuration) {
        this.vindicateSelfDuration = vindicateSelfDuration;
    }
}
