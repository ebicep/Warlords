package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Inferno extends AbstractAbility {
    protected int hitsAmplified = 0;

    private final int duration = 18;
    private int critChanceIncrease = 30;
    private int critMultiplierIncrease = 30;

    public Inferno() {
        super("Inferno", 0, 0, 46.98f, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Combust into a molten inferno,\n" +
                "§7increasing your Crit Chance by §c" + critChanceIncrease + "%\n" +
                "§7and your Crit Multiplier by §c" + critMultiplierIncrease + "%§7. Lasts\n" +
                "§6" + duration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hits Amplified", "" + hitsAmplified));

        return info;
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        Utils.playGlobalSound(player.getLocation(), "mage.inferno.activation", 2, 1);

        Inferno tempInferno = new Inferno();
        wp.getCooldownManager().addCooldown(new RegularCooldown<Inferno>(
                name,
                "INFR",
                Inferno.class,
                tempInferno,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                (cooldown, ticksLeft) -> {
                    if (ticksLeft % 3 == 0) {
                        Location location = wp.getLocation().add(0, 1.2, 0);
                        ParticleEffect.DRIP_LAVA.display(0.5F, 0.3F, 0.5F, 0.4F, 1, location, 500);
                        ParticleEffect.FLAME.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                        ParticleEffect.CRIT.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                    }
                }
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public int addCritChanceFromAttacker(WarlordsDamageHealingEvent event, int currentCritChance) {
                if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp"))
                    return currentCritChance;
                hitsAmplified++;
                return currentCritChance + critChanceIncrease;
            }

            @Override
            public int addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, int currentCritMultiplier) {
                if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp"))
                    return currentCritMultiplier;
                return currentCritMultiplier + critMultiplierIncrease;
            }
        });

        return true;
    }

    public int getHitsAmplified() {
        return hitsAmplified;
    }

    public int getCritChanceIncrease() {
        return critChanceIncrease;
    }

    public int getCritMultiplierIncrease() {
        return critMultiplierIncrease;
    }

    public void setCritChanceIncrease(int critChanceIncrease) {
        this.critChanceIncrease = critChanceIncrease;
    }

    public void setCritMultiplierIncrease(int critMultiplierIncrease) {
        this.critMultiplierIncrease = critMultiplierIncrease;
    }
}
