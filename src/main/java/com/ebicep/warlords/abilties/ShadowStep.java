package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ShadowStep extends AbstractAbility {
    protected int totalPlayersHit = 0;

    public ShadowStep() {
        super("Shadow Step", 466, 598, 12, 20, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Leap forward, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage to\n" +
                "§7all enemies close on cast or when landing\n" +
                "§7on the ground. You take reduced fall damage\n" +
                "§7while leaping." +
                "\n\n" +
                "§7Shadow Step has reduced range when\n" +
                "§7holding a Flag.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + totalPlayersHit));

        return info;
    }


    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        Location playerLoc = wp.getLocation();
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(playerLoc, "rogue.drainingmiasma.activation", 1, 2);
        Utils.playGlobalSound(playerLoc, Sound.AMBIENCE_THUNDER, 2, 2);

        wp.setFlagPickCooldown(2);
        if (wp.getCarriedFlag() != null) {
            player.setVelocity(playerLoc.getDirection().multiply(1).setY(0.35));
            player.setFallDistance(-5);
        } else {
            player.setVelocity(playerLoc.getDirection().multiply(1.5).setY(0.7));
            player.setFallDistance(-10);
        }


        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL)
                .build());

        if (wp.onHorse()) {
            wp.removeHorse();
        }

        List<WarlordsPlayer> playersHit = new ArrayList<>();
        for (WarlordsPlayer assaultTarget : PlayerFilter
                .entitiesAround(player, 5, 5, 5)
                .aliveEnemiesOf(wp)
        ) {
            totalPlayersHit++;
            assaultTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
            Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
            playersHit.add(assaultTarget);
        }

        new GameRunnable(wp.getGame()) {
            double y = playerLoc.getY();
            boolean wasOnGround = true;
            int counter = 0;
            @Override
            public void run() {
                counter++;
                // if player never lands in the span of 10 seconds, remove damage.
                if (counter == 200 || wp.isDead()) {
                    this.cancel();
                }

                wp.getLocation(playerLoc);
                boolean hitGround = player.isOnGround() || wp.onHorse();
                y = playerLoc.getY();

                if (wasOnGround && !hitGround) {
                    wasOnGround = false;
                }

                if (!wasOnGround && hitGround) {
                    wasOnGround = true;

                    for (WarlordsPlayer landingTarget : PlayerFilter
                            .entitiesAround(player, 5, 5, 5)
                            .aliveEnemiesOf(wp)
                            .excluding(playersHit)
                    ) {
                        totalPlayersHit++;
                        landingTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                        Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
                    }

                    FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                            .withColor(Color.BLACK)
                            .with(FireworkEffect.Type.BALL)
                            .build());

                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);

        return true;
    }
}
