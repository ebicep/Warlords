package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.FireWorkEffectPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class BlindingAssault extends AbstractAbility {

    public BlindingAssault() {
        super("Blinding Assault", 466, 612, 16, 0, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Leap forward, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage to\n" +
                "§7all enemies close to you. Enemies hit are blinded\n" +
                "§7for §61 §7second. Blinding Assault has reduced\n" +
                "§7range when holding a Flag.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        Location playerLoc = player.getLocation();

        if (wp.getCarriedFlag() != null) {
            player.setVelocity(playerLoc.getDirection().multiply(1.15).setY(0.4));
        } else {
            player.setVelocity(playerLoc.getDirection().multiply(1.5).setY(0.7));
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "rogue.drainingmiasma.activation", 1, 2);
            player1.playSound(playerLoc, Sound.AMBIENCE_THUNDER, 2, 2);
        }

        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL)
                .build());

        for (WarlordsPlayer assaultTarget : PlayerFilter
                .entitiesAround(player, 5, 5, 5)
                .aliveEnemiesOf(wp)
        ) {
            assaultTarget.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false), true);
            assaultTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
            }
        }

        return true;
    }
}
