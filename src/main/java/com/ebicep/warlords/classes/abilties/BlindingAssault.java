package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.FireWorkEffectPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlindingAssault extends AbstractAbility {

    public BlindingAssault() {
        super("Blinding Assault", 466, 598, 12, 20, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Leap forward, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage to\n" +
                "§7all enemies close on cast or when landing\n" +
                "§7on the ground. Enemies hit are blinded\n" +
                "§7for §61 §7second. You take reduced fall damage\n" +
                "§7while leaping." +
                "\n\n" +
                "§7Blinding Assault has reduced range when\n" +
                "§7holding a Flag.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        Location playerLoc = wp.getLocation();

        if (wp.getCarriedFlag() != null) {
            player.setVelocity(playerLoc.getDirection().multiply(1).setY(0.35));
            player.setFallDistance(-10);
        } else {
            player.setVelocity(playerLoc.getDirection().multiply(1.5).setY(0.7));
            player.setFallDistance(-10);
        }

        Utils.playGlobalSound(player.getLocation(), "rogue.drainingmiasma.activation", 1, 2);
        Utils.playGlobalSound(playerLoc, Sound.AMBIENCE_THUNDER, 2, 2);

        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL)
                .build());

        List<WarlordsPlayer> playersHit = new ArrayList<>();
        for (WarlordsPlayer assaultTarget : PlayerFilter
                .entitiesAround(player, 5, 5, 5)
                .aliveEnemiesOf(wp)
        ) {
            assaultTarget.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 0, true, false), true);
            assaultTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
            Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
            playersHit.add(assaultTarget);
        }

        new GameRunnable(wp.getGame()) {
            double y = playerLoc.getY();
            boolean wasOnGround = true;
            @Override
            public void run() {
                wp.getLocation(playerLoc);
                boolean hitGround = player.isOnGround();
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
                        landingTarget.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 0, true, false), true);
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
