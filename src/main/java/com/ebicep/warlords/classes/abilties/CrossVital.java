package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.EffectUtils;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CrossVital extends AbstractAbility {

    public CrossVital() {
        super("Cross Vital", 0, 0, 20, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Become invulnerable for §61 §7second. Any\n" +
                "§7incoming damage during this will be converted\n" +
                "§7into 50% energy and 50% critical chance. However,\n" +
                "§7not taking any damage during Cross Vital will\n" +
                "§7make you vulnerable, increasing the damage you\n" +
                "§7take by §c20% §7for §63 §7seconds.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "rogue.assassinstrike.activation", 2, 1.5f);
        }

        EffectUtils.playStarAnimation(player.getLocation(), 0.5f, ParticleEffect.FLAME);
    }
}
