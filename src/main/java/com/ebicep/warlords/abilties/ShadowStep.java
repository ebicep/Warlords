package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ShadowStep extends AbstractAbility {

    public int totalPlayersHit = 0;

    private int fallDamageNegation = 10;

    public ShadowStep() {
        super("Shadow Step", 466, 598, 12, 20, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Leap forward, dealing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage to all enemies close on cast or when landing on the ground. You take reduced fall damage while leaping." +
                "\n\nShadow Step has reduced range when holding a Flag.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + totalPlayersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Location playerLoc = wp.getLocation();
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(playerLoc, "rogue.drainingmiasma.activation", 1, 2);
        Utils.playGlobalSound(playerLoc, Sound.AMBIENCE_THUNDER, 2, 2);

        wp.setFlagPickCooldown(2);
        if (wp.getCarriedFlag() != null) {
            player.setVelocity(playerLoc.getDirection().multiply(1).setY(0.35));
            player.setFallDistance(-fallDamageNegation);
        } else {
            player.setVelocity(playerLoc.getDirection().multiply(1.5).setY(0.7));
            player.setFallDistance(-fallDamageNegation);
        }

        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL)
                .build());

        if (wp.onHorse()) {
            wp.removeHorse();
        }

        List<WarlordsEntity> playersHit = new ArrayList<>();
        for (WarlordsEntity assaultTarget : PlayerFilter
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

                    for (WarlordsEntity landingTarget : PlayerFilter
                            .entitiesAround(player, 5, 5, 5)
                            .aliveEnemiesOf(wp)
                            .excluding(playersHit)
                    ) {
                        totalPlayersHit++;
                        landingTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                        Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
                    }

                    if (pveUpgrade) {
                        buffOnLanding(wp);
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

    private void buffOnLanding(WarlordsEntity we) {
        we.getSpeed().addSpeedModifier(name, 20, 3 * 20);
        we.getCooldownManager().addCooldown(new RegularCooldown<ShadowStep>(
                "STEP KB",
                "STEP KB",
                ShadowStep.class,
                new ShadowStep(),
                we,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                3 * 20
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.multiply(0.8);
            }
        });
    }

    public void setFallDamageNegation(int fallDamageNegation) {
        this.fallDamageNegation = fallDamageNegation;
    }


}
