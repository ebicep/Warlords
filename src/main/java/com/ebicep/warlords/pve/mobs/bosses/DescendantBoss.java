package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

abstract class DescendantBoss extends AbstractMob implements BossMob {

    protected DescendantBoss(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                                                              .withColor(Color.fromRGB(120, 30, 180))
                                                              .with(FireworkEffect.Type.BALL_LARGE)
                                                              .withTrail()
                                                              .build());
    }

    protected void tickAura() {
        EffectUtils.playCylinderAnimation(warlordsNPC.getLocation(), 1.1, 110, 30, 180);
    }

    protected void drawRing(Location center, double radius, Particle particle) {
        new CircleEffect(
                warlordsNPC.getGame(),
                warlordsNPC.getTeam(),
                center.clone().add(0, 1, 0),
                radius,
                new CircumferenceEffect(particle)
        ).playEffects();
    }

    protected void damageAround(Location center, double radius, float damage, String cause) {
        PlayerFilter.entitiesAround(center, radius, radius, radius)
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(enemy -> enemy.addInstance(InstanceBuilder
                            .damage()
                            .cause(cause)
                            .source(warlordsNPC)
                            .value(damage)
                    ));
    }

    protected List<WarlordsEntity> enemies(double range) {
        return PlayerFilter.entitiesAround(warlordsNPC, range, range, range)
                           .aliveEnemiesOf(warlordsNPC)
                           .toList();
    }

    protected void announce(String message) {
        Component component = Component.text(message, NamedTextColor.DARK_PURPLE);
        PlayerFilter.playingGame(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(enemy -> enemy.sendMessage(component));
    }
}
