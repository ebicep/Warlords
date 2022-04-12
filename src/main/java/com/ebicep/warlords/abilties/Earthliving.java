package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Earthliving extends AbstractAbility {
    private final int duration = 8;
    protected int timesProcd = 0;
    protected int playersHealed = 0;
    private int procChance = 40;

    public Earthliving() {
        super("Earthliving Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Imbue your weapon with the power of the\n" +
                "§7Earth, causing each of your melee attacks\n" +
                "§7to have a §e" + procChance + "% §7chance to heal you and §e2\n" +
                "§7nearby allies for §a240% §7weapon damage.\n" +
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
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
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
                (cooldown, ticksLeft) -> {
                    if (ticksLeft % 4 == 0) {
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
                    WarlordsPlayer victim = event.getPlayer();
                    WarlordsPlayer attacker = event.getAttacker();

                    int earthlivingActivate = (int) (Math.random() * 100);
                    if (firstProc[0]) {
                        firstProc[0] = false;
                        earthlivingActivate = 0;
                    }
                    if (earthlivingActivate < procChance) {
                        timesProcd++;
                        Utils.playGlobalSound(victim.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);

                        attacker.addHealingInstance(
                                attacker,
                                name,
                                132 * 2.4f,
                                179 * 2.4f,
                                critChance,
                                critMultiplier,
                                false,
                                false
                        );

                        for (WarlordsPlayer nearPlayer : PlayerFilter
                                .entitiesAround(attacker, 6, 6, 6)
                                .aliveTeammatesOfExcludingSelf(attacker)
                                .limit(2)
                        ) {
                            playersHealed++;
                            nearPlayer.addHealingInstance(
                                    attacker,
                                    name,
                                    132 * 2.4f,
                                    179 * 2.4f,
                                    critChance,
                                    critMultiplier,
                                    false,
                                    false
                            );
                        }
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
}


