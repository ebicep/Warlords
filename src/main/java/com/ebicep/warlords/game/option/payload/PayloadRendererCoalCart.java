package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class PayloadRendererCoalCart implements PayloadRenderer {

    private final List<RenderStand> renderStands = new ArrayList<>();
    private Game game;
    private CircleEffect circleEffect = null;

    @Override
    public void init(@Nonnull Game game) {
        this.game = game;
        // body
        renderStands.add(new BarrelRenderStand(locationBuilder -> locationBuilder
                .forward(.5))
        );
        renderStands.add(new BarrelRenderStand(locationBuilder -> locationBuilder
                .forward(.2f)
                .right(.25f)
        ));
        renderStands.add(new BarrelRenderStand(locationBuilder -> locationBuilder
                .forward(.2f)
                .left(.25f)
        ));
        renderStands.add(new BarrelRenderStand(locationBuilder -> locationBuilder
                .backward(.2f)
                .right(.25f)
        ));
        renderStands.add(new BarrelRenderStand(locationBuilder -> locationBuilder
                .backward(.2f)
                .left(.25f)
        ));
        // wheels
        renderStands.add(new CoalWheelRenderStand(locationBuilder -> locationBuilder
                .addY(-.3)
                .forward(.5f)
                .right(.45f)
        ));
        renderStands.add(new CoalWheelRenderStand(locationBuilder -> locationBuilder
                .addY(-.3)
                .forward(.5f)
                .left(.45f)
        ));
        renderStands.add(new CoalWheelRenderStand(locationBuilder -> locationBuilder
                .addY(-.3)
                .backward(.5f)
                .right(.45f)
        ));
        renderStands.add(new CoalWheelRenderStand(locationBuilder -> locationBuilder
                .addY(-.3)
                .backward(.5f)
                .left(.45f)
        ));
    }

    @Override
    public void move(Location newCenter) {
        LocationBuilder center = new LocationBuilder(newCenter).addY(-.6);
        for (RenderStand renderStand : renderStands) {
            ArmorStand armorStand = renderStand.getArmorStand();
            if (armorStand == null || !armorStand.isValid()) {
                renderStand.respawn(center);
            } else {
                armorStand.teleport(renderStand.getRelativeLocation().apply(center.clone()));
            }
        }
    }

    @Override
    public void playEffects(int ticksElapsed, Location center, double radius) {
        if (circleEffect == null) {
            circleEffect = new CircleEffect(game, null, center, radius, .05, new CircumferenceEffect(Particle.CRIT).particles(10));
        }
        circleEffect.setCenter(center);
        circleEffect.playEffects();

        if (ticksElapsed % 5 == 0) {
            LocationBuilder smokeLocation = new LocationBuilder(center).addY(.2).backward(.6f);
            EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, smokeLocation, 1);
            EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, smokeLocation.addY(.5).backward(.1f), 1);
        }
    }

    @Override
    public void cleanup() {
        renderStands.forEach(renderStand -> renderStand.armorStand.remove());
    }

    public void addRenderPathRunnable(Game game, Location start, List<Location> path) {
        new GameRunnable(game) {

            static final double RENDER_MOVE = .5;
            Location displayLocation = start.clone();
            double i = 0;

            @Override
            public void run() {
                if (i >= path.size() - 1) {
                    displayLocation = start.clone();
                    i = 0;
                }
                Location nextPathLocation = path.get((int) (i + 1));
                Vector direction = nextPathLocation.toVector().subtract(displayLocation.toVector()).normalize();
                displayLocation.setDirection(direction);
                displayLocation.add(direction.multiply(RENDER_MOVE));
                EffectUtils.displayParticle(Particle.WATER_WAKE, displayLocation.clone().add(0, 1, 0), 1);
                i += RENDER_MOVE;
            }
        }.runTaskTimer(0, 0);
    }

    public void renderPath(List<Location> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            LocationBuilder location = new LocationBuilder(path.get(i));
            Location nextLocation = path.get(i + 1);
            // set vector facing nextLocation
            location.setDirection(nextLocation.toVector().subtract(location.toVector()).normalize());

            // render particle line towards nextLocation
            for (int j = 0; j < 10; j++) {
                EffectUtils.displayParticle(Particle.VILLAGER_HAPPY, location.clone().add(0, 1, 0), 1);
                location.forward(.1);
            }
        }
    }


    private static class RenderStand {
        private final Consumer<ArmorStand> consumer;
        private final UnaryOperator<LocationBuilder> relativeLocation;
        private ArmorStand armorStand = null;

        private RenderStand(Consumer<ArmorStand> consumer, UnaryOperator<LocationBuilder> relativeLocation) {
            this.consumer = consumer;
            this.relativeLocation = relativeLocation;
        }

        public void respawn(LocationBuilder center) {
            this.armorStand = Utils.spawnArmorStand(relativeLocation.apply(center), consumer);
        }

        public Consumer<ArmorStand> getConsumer() {
            return consumer;
        }

        public UnaryOperator<LocationBuilder> getRelativeLocation() {
            return relativeLocation;
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public void setArmorStand(ArmorStand armorStand) {
            this.armorStand = armorStand;
        }
    }

    private static final class BarrelRenderStand extends RenderStand {

        private BarrelRenderStand(UnaryOperator<LocationBuilder> relativeLocation) {
            super(armorStand -> armorStand.getEquipment().setHelmet(new ItemStack(Material.BARREL)), relativeLocation);
        }
    }

    private static final class CoalWheelRenderStand extends RenderStand {

        private CoalWheelRenderStand(UnaryOperator<LocationBuilder> relativeLocation) {
            super(armorStand -> {
                armorStand.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
                armorStand.setHeadPose(new EulerAngle(Math.toRadians(45), 0, 0));
            }, relativeLocation);
        }
    }

}