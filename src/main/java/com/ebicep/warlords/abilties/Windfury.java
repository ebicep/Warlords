package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Windfury extends AbstractAbility {
    private boolean pveUpgrade = false;
    protected int timesProcd = 0;

    private int procChance = 35;
    private final int duration = 8;
    private int maxHits = 2;
    private float weaponDamage = 135;

    public Windfury() {
        super("Windfury Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Imbue your weapon with the power\n" +
                "§7of the wind, causing each of your\n" +
                "§7melee attacks to have a §e" + procChance + "% §7chance\n" +
                "§7to hit §e" + maxHits + " §7additional times for §c" + weaponDamage + "%\n" +
                "§7weapon damage. The first melee hit is\n" +
                "§7guaranteed to activate Windfury. Lasts §6" + duration + "\n" +
                "§7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Proc'd", "" + timesProcd));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "shaman.windfuryweapon.activation", 2, 1);

        Windfury tempWindfury = new Windfury();
        final boolean[] firstProc = {true};
        wp.getCooldownManager().addCooldown(new RegularCooldown<Windfury>(
                name,
                "FURY",
                Windfury.class,
                tempWindfury,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 4 == 0) {
                        ParticleEffect.CRIT.display(
                                0.2f,
                                0,
                                0.2f,
                                0.1f,
                                3,
                                wp.getLocation().add(0, 1.2, 0),
                                500);
                    }
                }
        ) {
            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().isEmpty()) {
                    WarlordsEntity victim = event.getPlayer();
                    WarlordsEntity attacker = event.getAttacker();
                    float min = event.getMin();
                    float max = event.getMax();

                    int windfuryActivate = (int) (Math.random() * 100);
                    if (firstProc[0]) {
                        firstProc[0] = false;
                        windfuryActivate = 0;
                    }
                    if (windfuryActivate < procChance) {
                        timesProcd++;
                        new BukkitRunnable() {
                            int counter = 0;

                            @Override
                            public void run() {
                                Utils.playGlobalSound(victim.getLocation(), "shaman.windfuryweapon.impact", 2, 1);

                                if (Warlords.getPlayerSettings(attacker.getUuid()).getSkillBoostForClass() == SkillBoosts.WINDFURY_WEAPON) {
                                    victim.addDamageInstance(
                                            attacker,
                                            name,
                                            min * (weaponDamage / 100f) * 1.2f,
                                            max * (weaponDamage / 100f) * 1.2f,
                                            critChance,
                                            critMultiplier,
                                            false
                                    );
                                } else {
                                    victim.addDamageInstance(
                                            attacker,
                                            name,
                                            min * (weaponDamage / 100f),
                                            max * (weaponDamage / 100f),
                                            critChance,
                                            critMultiplier,
                                            false
                                    );
                                }

                                if (pveUpgrade) {
                                    victim.getSpec().setDamageResistance(victim.getSpec().getDamageResistance() - 2);
                                }

                                counter++;
                                if (counter == maxHits) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 3, 3);
                    }
                }
            }
        });

        return true;
    }

    public int getProcChance() {
        return procChance;
    }

    public void setProcChance(int procChance) {
        this.procChance = procChance;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }

    public float getWeaponDamage() {
        return weaponDamage;
    }

    public void setWeaponDamage(float weaponDamage) {
        this.weaponDamage = weaponDamage;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
