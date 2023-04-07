package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.abilties.internal.AbstractTotemBase;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChainLightning extends AbstractChainBase implements Comparable<ChainLightning> {

    public int numberOfDismounts = 0;

    private int damageReduction = 0;
    private int radius = 20;
    private int bounceRange = 10;
    private int maxBounces = 3;
    private float damageReductionPerBounce = 10;
    private float maxDamageReduction = 30;

    public ChainLightning() {
        super("Chain Lightning", 294, 575, 9.4f, 40, 20, 175);
    }

    public ChainLightning(int damageReduction) {
        super("Chain Lightning", 294, 575, 9.4f, 40, 20, 175);
        this.damageReduction = damageReduction;
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    @Override
    public void updateDescription(Player player) {
        description = "Discharge a bolt of lightning at the targeted enemy player that deals" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage and jumps to §e" + maxBounces + " §7additional targets within §e" + bounceRange +
                " §7blocks. Each time the lightning jumps the damage is decreased by §c15%§7. You gain §e" + format(damageReductionPerBounce) +
                "% §7damage resistance for each target hit, up to §e" + format(maxDamageReduction) +
                "% §7damage resistance. This buff lasts §64.5 §7seconds." +
                "\n\nHas an initial cast range of §e" + radius + " §7blocks.";
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
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp, Player player) {
        return partOfChainLightning(wp, new HashSet<>(), wp.getEntity(), false);
    }

    @Override
    protected void onHit(WarlordsEntity wp, Player player, int hitCounter) {
        Utils.playGlobalSound(player.getLocation(), "shaman.chainlightning.activation", 3, 1);
        player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);

        wp.getCooldownManager().removeCooldown(ChainLightning.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<ChainLightning>(
                name,
                "CHAIN",
                ChainLightning.class,
                new ChainLightning(hitCounter),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                4 * 20 + 10
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue;
                float multiplier = (((10 - hitCounter) / damageReductionPerBounce));
                if (multiplier > (100 - maxDamageReduction / 100f) && pveUpgrade) {
                    multiplier = (100 - maxDamageReduction / 100f);
                }
                newDamageValue = currentDamageValue * multiplier;
                event.getWarlordsEntity().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });
        wp.setRedCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.STAINED_GLASS, 1, (byte) 7);
    }

    private Set<WarlordsEntity> partOfChainLightning(WarlordsEntity wp, Set<WarlordsEntity> playersHit, Entity checkFrom, boolean hasHitTotem) {
        int playersSize = playersHit.size();
        if (playersSize >= (hasHitTotem ? maxBounces - 1 : maxBounces)) {
            if (hasHitTotem) {
                playersHit.add(null);
            }
            return playersHit;
        }

        boolean firstCheck = checkFrom == wp.getEntity();
        if (!hasHitTotem) {
            if (firstCheck) {
                Optional<CapacitorTotem> optionalTotem = getLookingAtTotem(wp);
                if (optionalTotem.isPresent()) {
                    ArmorStand totem = optionalTotem.get().getTotem();
                    chain(checkFrom.getLocation(), totem.getLocation());
                    partOfChainLightningPulseDamage(wp, optionalTotem.get());
                    playersHit.add(null);
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

        PlayerFilter filter = firstCheck ? PlayerFilter.entitiesAround(checkFrom, radius, 18, radius)
                .filter(e ->
                        Utils.isLookingAtChain(wp.getEntity(), e.getEntity()) &&
                                Utils.hasLineOfSight(wp.getEntity(), e.getEntity())
                ) : PlayerFilter.entitiesAround(checkFrom, bounceRange, bounceRange, bounceRange)
                .lookingAtFirst(wp);

        Optional<WarlordsEntity> foundPlayer = filter.closestFirst(wp).aliveEnemiesOf(wp).excluding(playersHit).findFirst();
        if (foundPlayer.isPresent()) {
            WarlordsEntity hit = foundPlayer.get();
            chain(checkFrom.getLocation(), hit.getLocation());
            float damageMultiplier;

            switch (playersSize) {
                case 0:
                    // We hit the first player
                    damageMultiplier = pveUpgrade ? 1.1f : 1f;
                    break;
                case 1:
                    // We hit the second player
                    damageMultiplier = pveUpgrade ? 1.2f : .85f;
                    break;
                default:
                    damageMultiplier = pveUpgrade ? 1.3f : .7f;
                    break;
            }

            playersHit.add(hit);
            if (hit.onHorse()) {
                numberOfDismounts++;
            }

            hit.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal * damageMultiplier,
                    maxDamageHeal * damageMultiplier,
                    critChance,
                    critMultiplier,
                    false
            );

            return partOfChainLightning(wp, playersHit, hit.getEntity(), hasHitTotem);
        } else {
            return playersHit;
        }
    }

    private void partOfChainLightningPulseDamage(WarlordsEntity wp, CapacitorTotem capacitorTotem) {
        ArmorStand totem = capacitorTotem.getTotem();
        capacitorTotem.pulseDamage();
        if (capacitorTotem.isPveUpgrade()) {
            capacitorTotem.setRadius(capacitorTotem.getRadius() + 0.5);
        }

        Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
        wp.playSound(totem.getLocation(), "shaman.chainlightning.impact", 2, 1);

        capacitorTotem.addProc();
    }

    private Optional<CapacitorTotem> getLookingAtTotem(WarlordsEntity warlordsPlayer) {
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(CapacitorTotem.class)
                .filter(totem -> totem.getTotem().getLocation().distanceSquared(warlordsPlayer.getLocation()) <= radius * radius
                        && totem.isPlayerLookingAtTotem(warlordsPlayer))
                .findFirst();
    }

    @Override
    public int compareTo(ChainLightning chainLightning) {
        return Integer.compare(this.damageReduction, chainLightning.damageReduction);
    }

    public float getDamageReductionPerBounce() {
        return damageReductionPerBounce;
    }

    public void setDamageReductionPerBounce(float damageReductionPerBounce) {
        this.damageReductionPerBounce = damageReductionPerBounce;
    }


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBounceRange() {
        return bounceRange;
    }

    public void setBounceRange(int bounceRange) {
        this.bounceRange = bounceRange;
    }

    public int getMaxBounces() {
        return maxBounces;
    }

    public void setMaxBounces(int maxBounces) {
        this.maxBounces = maxBounces;
    }

    public float getMaxDamageReduction() {
        return maxDamageReduction;
    }

    public void setMaxDamageReduction(float maxDamageReduction) {
        this.maxDamageReduction = maxDamageReduction;
    }
}
