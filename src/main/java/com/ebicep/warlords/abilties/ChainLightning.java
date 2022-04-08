package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChainLightning extends AbstractChainBase implements Comparable<ChainLightning> {

    protected int numberOfDismounts = 0;

    private int damageReduction = 0;

    private final int radius = 20;
    private final int bounceRange = 10;

    public int getDamageReduction() {
        return damageReduction;
    }

    public ChainLightning() {
        super("Chain Lightning", 294, 575, 9.4f, 40, 20, 175);
    }

    public ChainLightning(int damageReduction) {
        super("Chain Lightning", 294, 575, 9.4f, 40, 20, 175);
        this.damageReduction = damageReduction;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Discharge a bolt of lightning at the\n" +
                "§7targeted enemy player that deals\n" +
                "§c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage and jumps to\n" +
                "§e3 §7additional targets within §e" + bounceRange + "\n" +
                "§7blocks. Each time the lightning jumps\n" +
                "§7the damage is decreased by §c15%§7.\n" +
                "§7You gain §e10% §7damage resistance for\n" +
                "§7each target hit, up to §e30% §7damage\n" +
                "§7resistance. This buff lasts §64.5 §7seconds." +
                "\n\n" +
                "§7Has an initial cast range of §e" + radius + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    protected int getHitCounterAndActivate(WarlordsPlayer wp, Player player) {
        return partOfChainLightning(wp, new HashSet<>(), wp.getEntity(), false);
    }

    @Override
    protected void onHit(WarlordsPlayer warlordsPlayer, Player player, int hitCounter) {
        warlordsPlayer.getCooldownManager().removeCooldown(ChainLightning.class);
        warlordsPlayer.getCooldownManager().addCooldown(new RegularCooldown<ChainLightning>(
                name,
                "CHAIN",
                ChainLightning.class,
                new ChainLightning(hitCounter),
                warlordsPlayer,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                4 * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue = currentDamageValue * ((10 - hitCounter) / 10f);
                event.getPlayer().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });
        warlordsPlayer.getSpec().getRed().setCurrentCooldown((float) (cooldown * warlordsPlayer.getCooldownModifier()));

        player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);

        Utils.playGlobalSound(player.getLocation(), "shaman.chainlightning.activation", 3, 1);

    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.STAINED_GLASS, 1, (byte) 7);
    }

    private final int LIGHTING_MAX_PLAYERS_NO_TOTEM = 3;
    private final int LIGHTING_MAX_PLAYERS_WITH_TOTEM = 3;

    private int partOfChainLightning(WarlordsPlayer wp, Set<WarlordsPlayer> playersHit, Entity checkFrom, boolean hasHitTotem) {
        int playersSize = playersHit.size();
        if (playersSize >= (hasHitTotem ? LIGHTING_MAX_PLAYERS_WITH_TOTEM : LIGHTING_MAX_PLAYERS_NO_TOTEM)) {

            return playersSize + (hasHitTotem ? 1 : 0);
        }
        /**
         * The first check has double the radius for checking, and only targets a totem when the player is looking at it.
         */
        boolean firstCheck = checkFrom == wp.getEntity();
        if (!hasHitTotem) {
            if (firstCheck) {
                Optional<CapacitorTotem> optionalTotem = getLookingAtTotem(wp);
                if (checkFrom instanceof LivingEntity && optionalTotem.isPresent()) {
                    ArmorStand totem = optionalTotem.get().getTotem();
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(wp, optionalTotem.get());
                    return partOfChainLightning(wp, playersHit, totem, true);
                } // no else
            } else {
                Optional<CapacitorTotem> capacitorTotem = AbstractTotemBase.getTotemDownAndClose(wp, checkFrom, CapacitorTotem.class);
                if (capacitorTotem.isPresent()) {
                    ArmorStand totem = capacitorTotem.get().getTotem();
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(wp, capacitorTotem.get());
                    return partOfChainLightning(wp, playersHit, totem, true);
                } // no else
            }
        } // no else
        PlayerFilter filter = firstCheck ?
                PlayerFilter.entitiesAround(checkFrom, radius, 18, radius)
                        .filter(e ->
                                Utils.isLookingAtChain(wp.getEntity(), e.getEntity()) &&
                                        Utils.hasLineOfSight(wp.getEntity(), e.getEntity())
                        ) :
                PlayerFilter.entitiesAround(checkFrom, bounceRange, bounceRange, bounceRange).lookingAtFirst(wp);
        Optional<WarlordsPlayer> foundPlayer = filter.closestFirst(wp).aliveEnemiesOf(wp).excluding(playersHit).findFirst();
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
            if (hit.onHorse()) {
                numberOfDismounts++;
            }
            hit.addDamageInstance(wp, name, minDamageHeal * damageMultiplier, maxDamageHeal * damageMultiplier, critChance, critMultiplier, false);
            return partOfChainLightning(wp, playersHit, hit.getEntity(), hasHitTotem);
        } else {
            return playersSize + (hasHitTotem ? 1 : 0);
        }
    }

    private void partOfChainLightningPulseDamage(WarlordsPlayer wp, CapacitorTotem capacitorTotem) {
        ArmorStand totem = capacitorTotem.getTotem();
        capacitorTotem.pulseDamage();

        Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
        wp.playSound(totem.getLocation(), "shaman.chainlightning.impact", 2, 1);

        capacitorTotem.addProc();
    }

//
//    private boolean lookingAtTotem(@Nonnull LivingEntity player) {
//        Location eye = new LocationBuilder(player.getEyeLocation()).addY(.5).backward(1).get();
//        //eye.setY(eye.getY() + .5);
//        for (Entity entity : player.getNearbyEntities(20, 17, 20)) {
//            if (entity instanceof ArmorStand && entity.hasMetadata("capacitor-totem-" + player.getName().toLowerCase())) {
//                Vector toEntity = ((ArmorStand) entity).getEyeLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
//                float dot = (float) toEntity.normalize().dot(eye.getDirection());
//                return dot > .93f;
//            }
//        }
//        return false;
//    }

    private Optional<CapacitorTotem> getLookingAtTotem(WarlordsPlayer warlordsPlayer) {
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(CapacitorTotem.class)
                .filter(abstractTotemBase -> abstractTotemBase.isPlayerLookingAtTotem(warlordsPlayer))
                .findFirst();
    }

    @Override
    public int compareTo(ChainLightning chainLightning) {
        return Integer.compare(this.damageReduction, chainLightning.damageReduction);
    }
}
