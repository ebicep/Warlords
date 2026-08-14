package com.ebicep.warlords.pve.mobs.bosses.raidbosses;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.java.NumberFormat;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class RaidBossUtils {

    private static final int HEALTH_BAR_LENGTH = 30;

    private RaidBossUtils() {
    }

    public static RaidBossHealthBar createHealthBar(
            WarlordsNPC boss,
            float displaySize,
            double height,
            String name,
            Component subText,
            TextColor healthBarColor
    ) {
        return new RaidBossHealthBar(boss, displaySize, height, name, subText, healthBarColor);
    }

    public static final class RaidBossHealthBar {

        private final WarlordsNPC boss;
        private final float displaySize;
        private final double height;
        private final String name;
        private final Component subText;
        private final TextColor healthBarColor;
        private TextDisplay display;
        private int lastDisplayedHealth = Integer.MIN_VALUE;

        private RaidBossHealthBar(
                WarlordsNPC boss,
                float displaySize,
                double height,
                String name,
                Component subText,
                TextColor healthBarColor
        ) {
            this.boss = boss;
            this.displaySize = displaySize;
            this.height = height;
            this.name = name;
            this.subText = subText;
            this.healthBarColor = healthBarColor;

            hideDefaultDisplay();
            spawn();
        }

        public void update() {
            if (display == null || display.isDead()) {
                return;
            }
            if (!(boss.getEntity() instanceof LivingEntity entity)) {
                return;
            }

            display.teleport(getDisplayLocation(entity));

            int currentHealth = Math.round(boss.getCurrentHealth());
            if (currentHealth != lastDisplayedHealth) {
                display.text(buildHealthComponent());
                lastDisplayedHealth = currentHealth;
            }
        }

        public void remove() {
            if (display != null && !display.isDead()) {
                display.remove();
            }
            display = null;
        }

        public TextDisplay getDisplay() {
            return display;
        }

        private void hideDefaultDisplay() {
            boss.getMobHologram().setHidden(true);
            boss.getNpc().data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
            if (boss.getEntity() != null) {
                boss.getEntity().setCustomNameVisible(false);
            }
        }

        private void spawn() {
            if (!(boss.getEntity() instanceof LivingEntity entity)) {
                return;
            }

            Location location = getDisplayLocation(entity);
            display = entity.getWorld().spawn(location, TextDisplay.class, textDisplay -> {
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
                textDisplay.setDefaultBackground(true);
                textDisplay.setShadowed(true);
                textDisplay.setSeeThrough(false);
                textDisplay.setLineWidth(320);
                textDisplay.setViewRange(1.5f);
                textDisplay.setTeleportDuration(1);
                textDisplay.setPersistent(false);
                textDisplay.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new Quaternionf(),
                        new Vector3f(displaySize, displaySize, displaySize),
                        new Quaternionf()
                ));
                textDisplay.text(buildHealthComponent());
            });
            lastDisplayedHealth = Math.round(boss.getCurrentHealth());
        }

        private Location getDisplayLocation(LivingEntity entity) {
            return new Location(
                    entity.getWorld(),
                    (entity.getBoundingBox().getMinX() + entity.getBoundingBox().getMaxX()) / 2,
                    entity.getBoundingBox().getMaxY() + height,
                    (entity.getBoundingBox().getMinZ() + entity.getBoundingBox().getMaxZ()) / 2
            );
        }

        private Component buildHealthComponent() {
            double healthPercent = Math.max(0, Math.min(1, boss.getCurrentHealth() / boss.getMaxHealth()));
            int filled = (int) Math.round(HEALTH_BAR_LENGTH * healthPercent);
            filled = Math.max(0, Math.min(HEALTH_BAR_LENGTH, filled));

            String fullBar = "█".repeat(filled);
            String emptyBar = "█".repeat(HEALTH_BAR_LENGTH - filled);
            int percent = (int) Math.round(healthPercent * 100);

            TextComponent.Builder builder = Component.text();
            builder.append(Component.text(name, NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
            if (subText != null && !subText.equals(Component.empty())) {
                builder.append(Component.newline());
                builder.append(subText);
            }
            builder.append(Component.newline());
            builder.append(Component.text(fullBar, healthBarColor));
            builder.append(Component.text(emptyBar, NamedTextColor.DARK_GRAY));
            builder.append(Component.newline());
            builder.append(Component.text(
                    NumberFormat.addCommaAndRound(Math.round(boss.getCurrentHealth())) +
                            " / " +
                            NumberFormat.addCommaAndRound(Math.round(boss.getMaxHealth())) +
                            "  -  " +
                            percent + "%",
                    NamedTextColor.WHITE
            ));
            return builder.build();
        }
    }
}
