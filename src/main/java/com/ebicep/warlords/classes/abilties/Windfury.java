package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesSkillBoosts;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Windfury extends AbstractAbility {

    private boolean firstProc = false;
    private int procChance = 35;
    private final int duration = 8;

    public Windfury() {
        super("Windfury Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        int weaponDamage = procChance == 35 ? 135 : 155;
        description = "§7Imbue your weapon with the power\n" +
                "§7of the wind, causing each of your\n" +
                "§7melee attacks to have a §e" + procChance + "% §7chance\n" +
                "§7to hit §e2 §7additional times for §c" + weaponDamage + "%\n" +
                "§7weapon damage. The first melee hit is\n" +
                "§7guaranteed to activate Windfury. Lasts §6" + duration + "\n" +
                "§7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addCooldown(name, Windfury.this.getClass(), new Windfury(), "FURY", duration, wp, CooldownTypes.ABILITY);

        firstProc = true;

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.windfuryweapon.activation", 2, 1);
        }
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!wp.getCooldownManager().getCooldown(Windfury.class).isEmpty()) {
                            Location location = player.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.CRIT.display(0.2F, 0F, 0.2F, 0.1F, 3, location, 500);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 4),
                System.currentTimeMillis()
        );
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
