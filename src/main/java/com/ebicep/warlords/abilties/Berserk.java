package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Berserk extends AbstractAbility {
    private boolean pveUpgrade = false;

    protected int hitsDoneAmplified = 0;
    protected int hitsTakenAmplified = 0;

    private final int duration = 18;
    private int speedBuff = 30;
    private float damageIncrease = 30;
    private float damageTakenIncrease = 10;

    public Berserk() {
        super("Berserk", 0, 0, 46.98f, 30, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You go into a berserker rage,\n" +
                "§7increasing your damage by §c" + format(damageIncrease) + "% §7and\n" +
                "§7movement speed by §e" + speedBuff + "%§7. While active,\n" +
                "§7you also take §c" + format(damageTakenIncrease) + "% §7more damage.\n" + "§7Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hits Done Amplified", "" + hitsDoneAmplified));
        info.add(new Pair<>("Hits Taken Amplified", "" + hitsTakenAmplified));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(player.getLocation(), "warrior.berserk.activation", 2, 1);

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier(name, speedBuff, duration * 20, "BASE");

        Berserk tempBerserk = new Berserk();
        wp.getCooldownManager().addCooldown(new RegularCooldown<Berserk>(
                name,
                "BERS",
                Berserk.class,
                tempBerserk,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    cancelSpeed.run();
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 3 == 0) {
                        ParticleEffect.VILLAGER_ANGRY.display(
                                0,
                                0,
                                0,
                                0.1f,
                                1,
                                wp.getLocation().add(0, 1.75, 0),
                                500
                        );
                    }
                }
        ) {
            int multiplier = 0;
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsTakenAmplified++;
                return currentDamageValue * (1 + damageTakenIncrease / 100);
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsDoneAmplified++;
                multiplier++;
                return currentDamageValue * (1 + damageIncrease / 100);
            }

            int critBoost = (int) (0.2f * multiplier);

            @Override
            public int addCritChanceFromAttacker(WarlordsDamageHealingEvent event, int currentCritChance) {
                if (pveUpgrade) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritChance;
                    }
                    if (critBoost > 30) {
                        critBoost = 30;
                    }
                    return currentCritChance + critBoost;
                }
                return currentCritChance;
            }

            @Override
            public int addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, int currentCritMultiplier) {
                if (pveUpgrade) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritMultiplier;
                    }
                    if (critBoost > 30) {
                        critBoost = 30;
                    }
                    return currentCritMultiplier + critBoost;
                }
                return currentCritMultiplier;
            }
        });

        return true;
    }

    public float getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(float damageIncrease) {
        this.damageIncrease = damageIncrease;
    }

    public float getDamageTakenIncrease() {
        return damageTakenIncrease;
    }

    public void setDamageTakenIncrease(float damageTakenIncrease) {
        this.damageTakenIncrease = damageTakenIncrease;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
