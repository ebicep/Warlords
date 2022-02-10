package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Earthliving extends AbstractAbility {

    private final int duration = 8;
    private int procChance = 40;
    private boolean firstProc = false;

    public Earthliving() {
        super("Earthliving Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        int healthRestore = procChance == 40 ? 240 : 250;
        description = "§7Imbue your weapon with the power of the\n" +
                "§7Earth, causing each of your melee attacks\n" +
                "§7to have a §e" + procChance + "% §7chance to heal you and §e2\n" +
                "§7nearby allies for §a" + healthRestore + "% §7weapon damage.\n" +
                "§7Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7The first hit is guaranteed to activate Earthliving.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Earthliving tempEarthliving = new Earthliving();
        wp.getCooldownManager().addRegularCooldown(name, "EARTH", Earthliving.class, tempEarthliving, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);

        firstProc = true;

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
        }

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempEarthliving)) {
                    Location location = wp.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.VILLAGER_HAPPY.display(0.3F, 0.3F, 0.3F, 0.1F, 2, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 4);

        return true;
    }

    public boolean isFirstProc() {
        return firstProc;
    }

    public void setFirstProc(boolean firstProc) {
        this.firstProc = firstProc;
    }

    public int getProcChance() {
        return procChance;
    }

    public void setProcChance(int procChance) {
        this.procChance = procChance;
    }
}


