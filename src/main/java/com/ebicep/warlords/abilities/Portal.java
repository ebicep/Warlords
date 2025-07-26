package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Portal extends AbstractAbility implements PurpleAbilityIcon, AbilityStats<Portal, Portal.PortalStats> {

    private final PortalStats stats = new PortalStats();
    private int recastDelayTicks;

    public Portal() {
        super(AbstractAbilityBuilder.create("portal").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.recastDelayTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("recastDelayTicks"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Activate to place a portal on the ground with unlimited duration. After ")
                .durationTicks(recastDelayTicks)
                .text(", you can recast to teleport back to the portal location, removing the portal. You may not recast while holding the flag.")
                .build();
    }


    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);
        Portal.PortalData data = new Portal.PortalData(
                this,
                wp.getLocation()
        );
        PermanentCooldown<PortalData> portalCooldown = new PermanentCooldown<>(
                name,
                "PORTAL",
                PortalData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {},
                true,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 10 == 0) {
                        data.warpLocation.getWorld().spawnParticle(Particle.WITCH, data.warpLocation, 4, 0.1, 0, 0.1, 0.001, null, true);
                        int points = 6;
                        double radius = 0.5d;
                        for (int e = 0; e < points; e++) {
                            double angle = 2 * Math.PI * e / points;
                            Location point = data.warpLocation.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                            point.getWorld().spawnParticle(Particle.CHERRY_LEAVES, point, 2, 0.1, 0.3, 0.1, 0.001, null, true);
                        }
                    }
                }
        ) {
        };
        wp.getCooldownManager().addCooldown(portalCooldown);
        addSecondaryAbility(
                recastDelayTicks,
                () -> {
                    if (wp.isDead() || wp.getGame().getState() instanceof EndState) {
                        return;
                    }
                    if (wp.hasFlag()) {
                        wp.sendMessage(Component.text("You cannot teleport with the flag!", NamedTextColor.RED));
                        return;
                    }
                    Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 1, 1);
                    Location loc = wp.getLocation();
                    wp.getEntity().teleport(data.warpLocation);
                    stats.distanceTeleported += (float) loc.distance(data.warpLocation);
                    wp.getCooldownManager().removeCooldown(portalCooldown);
                },
                true,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(portalCooldown)
        );
        return true;
    }

    @Override
    public PortalStats getAbilityStats() {
        return stats;
    }

    public static class PortalData {

        private final Portal portal;
        private final Location warpLocation;

        public PortalData(Portal portal, Location warpLocation) {
            this.portal = portal;
            this.warpLocation = warpLocation;
        }

        public Portal getPortal() {
            return portal;
        }

        public Location getWarpLocation() {
            return warpLocation;
        }

    }

    public static class PortalStats extends AbstractAbilityStats<Portal, PortalStats> {

        @Field("distance_teleported")
        private float distanceTeleported = 0;

        @Override
        public Class<PortalStats> getClazz() {
            return PortalStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Distance Teleported", distanceTeleported));
            return statsDisplay;
        }

        @Override
        public PortalStats merge(PortalStats other, int multiplier) {
            PortalStats stats = super.merge(other, multiplier);
            stats.distanceTeleported = this.distanceTeleported + other.distanceTeleported * multiplier;
            return stats;
        }

        @Override
        public PortalStats create() {
            return new PortalStats();
        }

    }
}
