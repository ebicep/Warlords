package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import java.util.HashSet;
import java.util.Optional;
import com.ebicep.warlords.util.PlayerFilter;
import java.util.*;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.LivingEntity;

public class Chain extends AbstractAbility {

    public Chain(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

        @Override
    public void updateDescription() {
        if (name.contains("Lightning")) {
            description = "§7Discharge a bolt of lightning at the\n" +
                    "§7targeted enemy player that deals\n" +
                    "§c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage and jumps to\n" +
                    "§e4 §7additional targets within §e15\n" +
                    "§7blocks. Each time the lightning jumps\n" +
                    "§7the damage is decreased by §c15%§7.\n" +
                    "§7You gain §e10% §7damage resistance for\n" +
                    "§7each target hit, up to §e30% §7damage\n" +
                    "§7resistance. This buff lasts §64.5 §7seconds.";
        } else if (name.contains("Heal")) {
            description = "§7Discharge a beam of energizing lightning\n" +
                    "§7that heals you and a targeted friendly\n" +
                    "§7player for §a" + minDamageHeal + " §7- §a" + maxDamageHeal + " §7health and\n" +
                    "§7jumps to §e2 §7additional targets within\n" +
                    "§e10 §7blocks." +
                    "\n\n" +
                    "§7Each ally healed reduces the cooldown of\n" +
                    "§7Boulder by §62 §7seconds.";
        } else if (name.contains("Spirit")) {
            description = "§7Links your spirit with up to §c3 §7enemy\n" +
                    "§7players, dealing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7to the first target hit. Each additional hit\n" +
                    "§7deals §c10% §7reduced damage. You gain §e40%\n" +
                    "§7speed for §61.5 §7seconds, and take §c20%\n" +
                    "§7reduced damage for §64.5 §7seconds.";
        }
    }

    private void partOfChainLightningPulseDamage(WarlordsPlayer warlordsPlayer, Entity totem) {
        pulseDamage(warlordsPlayer, PlayerFilter.entitiesAround(totem, 4, 4, 4).aliveEnemiesOf(warlordsPlayer).stream());
    }

    private final int LIGHTING_MAX_PLAYERS_NO_TOTEM = 2;
    private final int LIGHTING_MAX_PLAYERS_WITH_TOTEM = 2;


    private int partOfChainLightning(WarlordsPlayer warlordsPlayer, Set<WarlordsPlayer> playersHit, Entity checkFrom, boolean hasHitTotem) {
        int playersSize = playersHit.size();
        if (playersSize >= (hasHitTotem ? LIGHTING_MAX_PLAYERS_WITH_TOTEM : LIGHTING_MAX_PLAYERS_NO_TOTEM)) {
            return playersSize;
        }
        /**
         * The first check has double the radius for checking, and only targets a totem when the player is looking at it.
         */
        boolean firstCheck = checkFrom == warlordsPlayer.getEntity();
        if (!hasHitTotem) {
            if (firstCheck) {
                if (checkFrom instanceof LivingEntity && lookingAtTotem((LivingEntity)checkFrom)) {
                    ArmorStand totem = getTotem(warlordsPlayer);
                    partOfChainLightningPulseDamage(warlordsPlayer, totem);
                    return partOfChainLightning(warlordsPlayer, playersHit, totem, true);
                } // no else
            } else {
                ArmorStand totem = Utils.getTotemDownAndClose(warlordsPlayer, checkFrom);
                if (totem != null) {
                    partOfChainLightningPulseDamage(warlordsPlayer, totem);
                    return partOfChainLightning(warlordsPlayer, playersHit, totem, true);
                } // no else
            }
        } // no else
        PlayerFilter filter = firstCheck ?
            PlayerFilter.entitiesAround(checkFrom, 20, 18, 20)
                .filter(e ->
                    Utils.getLookingAtChain(warlordsPlayer.getEntity(), e.getEntity()) &&
                    Utils.hasLineOfSight(warlordsPlayer.getEntity(), e.getEntity())
                ) :
            PlayerFilter.entitiesAround(checkFrom, 10, 9, 10);
        Optional<WarlordsPlayer> foundPlayer = filter.closestFirst(warlordsPlayer).aliveEnemiesOf(warlordsPlayer).excluding(playersHit).findFirst();
        if (foundPlayer.isPresent()) {
            WarlordsPlayer hit = foundPlayer.get();
            float damageMultiplier;
            switch(playersSize) {
                case 0:
                    // We hit the first player
                    damageMultiplier = 1f;
                    break;
                case 1:
                    // We hit the second player
                    damageMultiplier = .85f;
                    break;
                default:
                    damageMultiplier = .7f;
                    break;
            }
            playersHit.add(hit);
            hit.addHealth(warlordsPlayer, name, minDamageHeal * damageMultiplier, maxDamageHeal * damageMultiplier, critChance, critMultiplier);
            return partOfChainLightning(warlordsPlayer, playersHit, hit.getEntity(), hasHitTotem);
        } else {
            return playersSize;
        }
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        //TODO add Your enemy is too far away!

        /* CHAINS
           TOTEM -> PLAYER -> PLAYER
           PLAYER -> TOTEM -> PLAYER
           PLAYER -> PLAYER -> TOTEM
           PLAYER -> PLAYER -> PLAYER
         */
        int hitCounter = 0;
        if (name.contains("Lightning")) {
            hitCounter = partOfChainLightning(warlordsPlayer, new HashSet<>(), warlordsPlayer.getEntity(), false);
        } else if (name.contains("Heal")) {
            for (WarlordsPlayer nearPlayer : PlayerFilter.entitiesAround(player, 10, 9, 10).aliveTeammatesOf(warlordsPlayer)) {
                if (Utils.getLookingAtChain(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                    //self heal
                    warlordsPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    chain(player.getLocation(), nearPlayer.getLocation());
                    nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    hitCounter++;

                    for (WarlordsPlayer nearNearPlayer : PlayerFilter
                        .entitiesAround(nearPlayer, 5.0D, 4.0D, 5.0D)
                        .aliveTeammatesOf(warlordsPlayer)
                        .excluding(warlordsPlayer, nearPlayer)
                    ) {
                        chain(nearPlayer.getLocation(), nearNearPlayer.getLocation());
                        nearNearPlayer.addHealth(warlordsPlayer, name, minDamageHeal * .9f, maxDamageHeal * .9f, critChance, critMultiplier);
                        hitCounter++;

                        for (WarlordsPlayer nearNearNearPlayer : PlayerFilter
                            .entitiesAround(nearNearPlayer, 5.0D, 4.0D, 5.0D)
                            .aliveTeammatesOf(warlordsPlayer)
                            .excluding(warlordsPlayer, nearPlayer, nearNearPlayer)
                        ) {
                            chain(nearNearPlayer.getLocation(), nearNearNearPlayer.getLocation());
                            nearNearPlayer.addHealth(warlordsPlayer, name, minDamageHeal * .8f, maxDamageHeal * .8f, critChance, critMultiplier);
                            hitCounter++;
                            break;
                        }
                        break;
                    }
                    break;
                }
            }
        } else if (name.contains("Spirit")) {
            for (WarlordsPlayer nearPlayer : PlayerFilter
                .entitiesAround(player, 15.0D, 13.0D, 15.0D)
                .aliveEnemiesOf(warlordsPlayer)
            ) {
                if (Utils.getLookingAtChain(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                    chain(player.getLocation(), nearPlayer.getLocation());
                    nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    hitCounter++;

                    if (warlordsPlayer.hasBoundPlayerLink(nearPlayer)) {
                        healNearPlayers(warlordsPlayer);
                    }
                    for (WarlordsPlayer nearNearPlayer : PlayerFilter
                        .entitiesAround(nearPlayer, 10.0D, 9.0D, 10.0D)
                        .aliveEnemiesOf(warlordsPlayer)
                        .excluding(nearPlayer)
                    ) {
                        chain(nearPlayer.getLocation(), nearNearPlayer.getLocation());
                        nearNearPlayer.addHealth(warlordsPlayer, name, minDamageHeal * .8f, maxDamageHeal * .8f, critChance, critMultiplier);
                        hitCounter++;

                        if (warlordsPlayer.hasBoundPlayerLink(nearNearPlayer)) {
                            healNearPlayers(warlordsPlayer);
                        }
                        for (WarlordsPlayer nearNearNearPlayer : PlayerFilter
                            .entitiesAround(nearNearPlayer, 10.0D, 9.0D, 10.0D)
                            .aliveEnemiesOf(warlordsPlayer)
                            .excluding(nearPlayer, nearNearPlayer)
                        ) {
                            chain(nearNearPlayer.getLocation(), nearNearNearPlayer.getLocation());
                            nearNearPlayer.addHealth(warlordsPlayer, name, minDamageHeal * .6f, maxDamageHeal * .6f, critChance, critMultiplier);
                            hitCounter++;

                            if (warlordsPlayer.hasBoundPlayerLink(nearNearNearPlayer)) {
                                healNearPlayers(warlordsPlayer);
                            }
                            break;
                        }
                        break;
                    }
                    break;
                }
            }
        }

        if (hitCounter != 0) {
            PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
            warlordsPlayer.subtractEnergy(energyCost);
            if (name.contains("Lightning")) {
                warlordsPlayer.setChainLightning(hitCounter);
                warlordsPlayer.setChainLightningCooldown(4);
                warlordsPlayer.getSpec().getRed().setCurrentCooldown(cooldown);

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "shaman.chainlightning.activation", 2, 1);
                }
                player.playSound(player.getLocation(), "shaman.chainlightning.impact", 1F, 1);

            } else if (name.contains("Heal")) {
                if (hitCounter * 2 > warlordsPlayer.getSpec().getRed().getCurrentCooldown()) {
                    warlordsPlayer.getSpec().getRed().setCurrentCooldown(0);
                } else {
                    warlordsPlayer.getSpec().getRed().setCurrentCooldown(warlordsPlayer.getSpec().getRed().getCurrentCooldown() - hitCounter * 2);
                }
                warlordsPlayer.updateRedItem(player);
                warlordsPlayer.getSpec().getBlue().setCurrentCooldown(cooldown);

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "shaman.chainheal.activation", 2, 1);
                }
                warlordsPlayer.updateBlueItem(player);
            } else if (name.contains("Spirit")) {
                // speed buff
                warlordsPlayer.getSpeed().addSpeedModifier("Spirit Link", 40, 30); // 30 is ticks
                warlordsPlayer.setSpiritLink(30);

                warlordsPlayer.getSpec().getRed().setCurrentCooldown(cooldown);

                player.playSound(player.getLocation(), "mage.firebreath.activation", 1.5F, 1);
            }
        }

    }

    private void pulseDamage(WarlordsPlayer warlordsPlayer, Stream<WarlordsPlayer> near) {
        near.forEach((player) -> {
            player.addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
        });
    }

    private void healNearPlayers(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.addHealth(warlordsPlayer, warlordsPlayer.getSpec().getRed().getName(), 420, 420, -1, 100);
        for (WarlordsPlayer nearPlayer : PlayerFilter
            .entitiesAround(warlordsPlayer, 2.5D, 2D, 2.5D)
            .aliveTeammatesOf(warlordsPlayer)
            .limit(2)
        ) {
            nearPlayer.addHealth(warlordsPlayer, "Soulbinding Weapon", 420, 420, -1, 100);
        }
    }

    private void chain(Entity from, WarlordsPlayer to) {
        chain(from.getLocation(), to.getLocation());
    }
    private void chain(Location from, Location to) {
        Location location = from.subtract(0, .5, 0);
        location.setDirection(location.toVector().subtract(to.subtract(0, .5, 0).toVector()).multiply(-1));
        spawnChain((int) Math.round(from.distance(to)), location);
    }

    private void spawnChain(int distance, Location location) {

        List<ArmorStand> chains = new ArrayList<>();

        if (name.contains("Lightning")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setGravity(false);
                chain.setVisible(false);
                chain.setMarker(true);
                chain.setHelmet(new ItemStack(Material.RED_MUSHROOM));
                location.add(location.getDirection().multiply(1.2));
                chains.add(chain);
            }
        } else if (name.contains("Heal")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setGravity(false);
                chain.setVisible(false);
                chain.setMarker(true);
                chain.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 1));
                location.add(location.getDirection().multiply(1.2));
                chains.add(chain);
            }
        } else {//if (name.contains("Spirit")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setGravity(false);
                chain.setVisible(false);
                chain.setMarker(true);
                chain.setHelmet(new ItemStack(Material.SPRUCE_FENCE_GATE));
                location.add(location.getDirection().multiply(1.2));
                chains.add(chain);
            }
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                if (chains.size() == 0) {
                    this.cancel();
                }

                for (int i = 0; i < chains.size(); i++) {
                    ArmorStand armorStand = chains.get(i);
                    if (armorStand.getTicksLived() > 12) {
                        armorStand.remove();
                        chains.remove(i);
                        i--;
                    }
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    private boolean lookingAtTotem(@Nonnull LivingEntity player) {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY() + .5);
        for (Entity entity : player.getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Capacitor Totem - " + player.getName())) {
                Vector toEntity = ((ArmorStand) entity).getEyeLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
                float dot = (float) toEntity.normalize().dot(eye.getDirection());
                return dot > .95f;
            }
        }
        return false;
    }

    @Nullable
    private ArmorStand getTotem(@Nonnull WarlordsPlayer player) {
        for (Entity entity : player.getEntity().getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Capacitor Totem - " + player.getName())) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }

}
