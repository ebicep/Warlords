package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractChainBase;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ChainLightning extends AbstractChainBase {

    public ChainLightning() {
        super("Chain Lightning", -294, -575, 9.4f, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Discharge a bolt of lightning at the\n" +
                "§7targeted enemy player that deals\n" +
                "§c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage and jumps to\n" +
                "§e4 §7additional targets within §e15\n" +
                "§7blocks. Each time the lightning jumps\n" +
                "§7the damage is decreased by §c15%§7.\n" +
                "§7You gain §e10% §7damage resistance for\n" +
                "§7each target hit, up to §e30% §7damage\n" +
                "§7resistance. This buff lasts §64.5 §7seconds.";
    }

    @Override
    protected int getHitCounterAndActivate(WarlordsPlayer warlordsPlayer, Player player) {
        return partOfChainLightning(warlordsPlayer, new HashSet<>(), warlordsPlayer.getEntity(), false);
    }

    @Override
    protected void onHit(WarlordsPlayer warlordsPlayer, Player player, int hitCounter) {
        warlordsPlayer.getCooldownManager().addCooldown(this.getClass(), new ChainLightning(), "CHAIN(" + hitCounter + ")", 4, warlordsPlayer, CooldownTypes.BUFF);
        warlordsPlayer.getSpec().getRed().setCurrentCooldown((float) (cooldown * warlordsPlayer.getCooldownModifier()));

        player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.chainlightning.activation", 2, 1);
        }
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.RED_MUSHROOM);
    }

    private final int LIGHTING_MAX_PLAYERS_NO_TOTEM = 3;
    private final int LIGHTING_MAX_PLAYERS_WITH_TOTEM = 3;

    private int partOfChainLightning(WarlordsPlayer warlordsPlayer, Set<WarlordsPlayer> playersHit, Entity checkFrom, boolean hasHitTotem) {
        int playersSize = playersHit.size();
        if (playersSize >= (hasHitTotem ? LIGHTING_MAX_PLAYERS_WITH_TOTEM : LIGHTING_MAX_PLAYERS_NO_TOTEM)) {

            return playersSize + (hasHitTotem ? 1 : 0);
        }
        /**
         * The first check has double the radius for checking, and only targets a totem when the player is looking at it.
         */
        boolean firstCheck = checkFrom == warlordsPlayer.getEntity();
        if (!hasHitTotem) {
            if (firstCheck) {
                if (checkFrom instanceof LivingEntity && lookingAtTotem((LivingEntity) checkFrom)) {
                    ArmorStand totem = getTotem(warlordsPlayer);
                    assert totem != null;
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(warlordsPlayer, totem);
                    return partOfChainLightning(warlordsPlayer, playersHit, totem, true);
                } // no else
            } else {
                ArmorStand totem = Utils.getTotemDownAndClose(warlordsPlayer, checkFrom);
                if (totem != null) {
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(warlordsPlayer, totem);
                    return partOfChainLightning(warlordsPlayer, playersHit, totem, true);
                } // no else
            }
        } // no else
        PlayerFilter filter = firstCheck ?
                PlayerFilter.entitiesAround(checkFrom, 20, 18, 20)
                        .filter(e ->
                                Utils.isLookingAtChain(warlordsPlayer.getEntity(), e.getEntity()) &&
                                        Utils.hasLineOfSight(warlordsPlayer.getEntity(), e.getEntity())
                        ) :
                PlayerFilter.entitiesAround(checkFrom, 15, 14, 15);
        Optional<WarlordsPlayer> foundPlayer = filter.closestFirst(warlordsPlayer).aliveEnemiesOf(warlordsPlayer).excluding(playersHit).findFirst();
        if (foundPlayer.isPresent()) {
            WarlordsPlayer hit = foundPlayer.get();
            chain(checkFrom.getLocation(), hit.getLocation());
            float damageMultiplier;
            switch (playersSize) {
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
            return playersSize + (hasHitTotem ? 1 : 0);
        }
    }

    private void partOfChainLightningPulseDamage(WarlordsPlayer wp, Entity totem) {
        pulseDamage(wp, PlayerFilter.entitiesAround(totem, 5, 4, 5).aliveEnemiesOf(wp).stream());
        new FallingBlockWaveEffect(totem.getLocation().add(0, 1, 0), 6, 1.2, Material.SAPLING, (byte) 0).play();
        for (Player player1 : wp.getWorld().getPlayers()) {
            player1.playSound(wp.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
        }
    }

    private void pulseDamage(WarlordsPlayer warlordsPlayer, Stream<WarlordsPlayer> near) {
        near.forEach((player) -> {
            player.addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
        });
    }

    private boolean lookingAtTotem(@Nonnull LivingEntity player) {
        Location eye = player.getEyeLocation();
        //eye.setY(eye.getY() + .5);
        for (Entity entity : player.getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("capacitor-totem-" + player.getName().toLowerCase())) {
                Vector toEntity = ((ArmorStand) entity).getEyeLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
                float dot = (float) toEntity.normalize().dot(eye.getDirection());
                return dot > .93f;
            }
        }
        return false;
    }

    @Nullable
    private ArmorStand getTotem(@Nonnull WarlordsPlayer player) {
        for (Entity entity : player.getEntity().getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("capacitor-totem-" + player.getName().toLowerCase())) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }
}
