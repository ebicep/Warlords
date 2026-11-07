package com.ebicep.warlords.supporter;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.effects.EffectUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SupporterCelebration {

    private SupporterCelebration() {
    }

    public static void onPermissionsSynced(UUID uuid, DatabasePlayer lifetime, boolean wasSupporter, boolean nowSupporter) {
        if (!nowSupporter) {
            lifetime.setPendingSupporterCelebration(false);
            return;
        }
        Player player = Bukkit.getPlayer(uuid);
        boolean gainedSupporter = !wasSupporter;
        if (!gainedSupporter && !lifetime.isPendingSupporterCelebration()) {
            return;
        }
        if (player == null) {
            if (gainedSupporter) {
                lifetime.setPendingSupporterCelebration(true);
            }
            return;
        }
        lifetime.setPendingSupporterCelebration(false);
        celebrate(player);
    }

    private static void celebrate(Player player) {
        Runnable celebration = () -> {
            if (!player.isOnline()) {
                return;
            }
            player.sendMessage(Component.text("Thank you for supporting Warlords! Your Supporter rank is now active.", NamedTextColor.GOLD));
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
            EffectUtils.playFirework(player.getLocation().clone().add(0, 1, 0), FireworkEffect.builder()
                    .withColor(Color.YELLOW)
                    .withColor(Color.ORANGE)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withTrail()
                    .build());
        };
        if (Bukkit.isPrimaryThread()) {
            celebration.run();
        } else {
            Bukkit.getScheduler().runTask(Warlords.getInstance(), celebration);
        }
    }
}
