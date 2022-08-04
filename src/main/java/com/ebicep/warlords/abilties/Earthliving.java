package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Earthliving extends AbstractAbility {
    protected int timesProcd = 0;
    protected int playersHealed = 0;

    private final int duration = 8;
    private int procChance = 40;
    private int maxAllies = 2;
    private int weaponDamage = 240;
    private int maxHits = 1;

    public Earthliving() {
        super("Earthliving Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Imbue your weapon with the power of the\n" +
                "§7Earth, causing each of your melee attacks\n" +
                "§7to have a §e" + procChance + "% §7chance to heal you and §e2\n" +
                "§7nearby allies for §a" + weaponDamage + "% §7weapon damage.\n" +
                "§7Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7The first hit is guaranteed to activate Earthliving.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Proc'd", "" + timesProcd));
        info.add(new Pair<>("Players Healed", "" + playersHealed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);

        Earthliving tempEarthliving = new Earthliving();
        final boolean[] firstProc = {true};
        wp.getCooldownManager().addCooldown(new RegularCooldown<Earthliving>(
                name,
                "EARTH",
                Earthliving.class,
                tempEarthliving,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 4 == 0) {
                        ParticleEffect.VILLAGER_HAPPY.display(
                                0.3f,
                                0.3f,
                                0.3f,
                                0.1f,
                                2,
                                wp.getLocation().add(0, 1.2, 0),
                                500
                        );
                    }
                }
        ) {
            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().isEmpty()) {
                    WarlordsEntity victim = event.getPlayer();
                    WarlordsEntity attacker = event.getAttacker();

                    int earthlivingActivate = (int) (Math.random() * 100);
                    if (firstProc[0]) {
                        firstProc[0] = false;
                        earthlivingActivate = 0;
                    }
                    if (earthlivingActivate < procChance) {
                        new BukkitRunnable() {
                            int counter = 0;

                            @Override
                            public void run() {
                                timesProcd++;
                                Utils.playGlobalSound(victim.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);

                                attacker.addHealingInstance(
                                        attacker,
                                        name,
                                        132 * (weaponDamage / 100f),
                                        179 * (weaponDamage / 100f),
                                        critChance,
                                        critMultiplier,
                                        false,
                                        false
                                );

                                for (WarlordsEntity nearPlayer : PlayerFilter
                                        .entitiesAround(attacker, 6, 6, 6)
                                        .aliveTeammatesOfExcludingSelf(attacker)
                                        .limit(maxAllies)
                                ) {
                                    playersHealed++;
                                    nearPlayer.addHealingInstance(
                                            attacker,
                                            name,
                                            132 * (weaponDamage / 100f),
                                            179 * (weaponDamage / 100f),
                                            critChance,
                                            critMultiplier,
                                            false,
                                            false
                                    );
                                }

                                counter++;
                                if (counter == maxHits) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 3, 8);
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

    public int getWeaponDamage() {
        return weaponDamage;
    }

    public void setWeaponDamage(int weaponDamage) {
        this.weaponDamage = weaponDamage;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies) {
        this.maxAllies = maxAllies;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }
}


