package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Repentance extends AbstractAbility {

    private float pool = 0;
    private int damageConvertPercent = 10;
    private final int duration = 12;

    public Repentance() {
        super("Repentance", 0, 0, 31.32f, 20, 0, 0);
        Warlords.game.getGameTasks().put(

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (pool > 0) {
                            float newPool = pool * .8f - 60;
                            pool = Math.max(newPool, 0);
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Taking damage empowers your damaging\n" +
                "§7abilities and melee hits, restoring health\n" +
                "§7and energy based on §c" + damageConvertPercent + " §7+ §c10% §7of the\n" +
                "§7damage you've recently took. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        pool += 2000;
        assert warlordsPlayer != null;
        warlordsPlayer.getCooldownManager().addCooldown(name, Repentance.this.getClass(), new Repentance(), "REPE", duration, warlordsPlayer, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.barrieroflight.impact", 2, 1.35f);
        }

        Location playerLoc = player.getLocation();
        Location particleLoc = playerLoc.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                double width = 1;
                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                particleLoc.setY(playerLoc.getY() + i / 5D);
                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 255, 255), particleLoc, 500);
            }
        }
    }

    public float getPool() {
        return pool;
    }

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    public void addToPool(float amount) {
        this.pool += amount;
    }

    public void setPool(float pool) {
        this.pool = pool;
    }
}
