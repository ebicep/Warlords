package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SpiritLink extends AbstractChainBase {

    public int numberOfDismounts = 0;

    private int bounceRange = 10;
    private double speedDuration = 1.5;
    private double damageReductionDuration = 4.5;

    public SpiritLink() {
        super("Spirit Link", 236.25f, 446.25f, 8.61f, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Links your spirit with up to §c3 §7enemy players, dealing " + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage to the first target hit. Each additional hit deals §c20% §7reduced damage. You gain §e40% §7speed for §6" + speedDuration +
                " §7seconds, and take §c15% §7reduced damage for §6" + damageReductionDuration + " §7seconds.";
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
        Set<WarlordsEntity> hitCounter = new HashSet<>();
        for (WarlordsEntity nearPlayer : PlayerFilter
                .entitiesAround(player, 20, 18, 20)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .soulBindedFirst(wp)
        ) {
            if (Utils.isLookingAtChain(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                playersHit++;
                if (nearPlayer.onHorse()) {
                    numberOfDismounts++;
                }
                chain(player.getLocation(), nearPlayer.getLocation());
                nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                hitCounter.add(nearPlayer);

                int numberOfHeals = wp.getCooldownManager().getNumberOfBoundPlayersLink(nearPlayer);
                for (int i = 0; i < numberOfHeals; i++) {
                    healNearPlayers(wp, nearPlayer);
                }

                for (WarlordsEntity chainPlayerOne : PlayerFilter
                        .entitiesAround(nearPlayer, bounceRange, bounceRange, bounceRange)
                        .aliveEnemiesOf(wp)
                        .excluding(nearPlayer)
                        .soulBindedFirst(wp)
                ) {
                    playersHit++;
                    if (chainPlayerOne.onHorse()) {
                        numberOfDismounts++;
                    }
                    chain(nearPlayer.getLocation(), chainPlayerOne.getLocation());
                    chainPlayerOne.addDamageInstance(wp, name, minDamageHeal * .8f, maxDamageHeal * .8f, critChance, critMultiplier, false);
                    hitCounter.add(chainPlayerOne);

                    numberOfHeals = wp.getCooldownManager().getNumberOfBoundPlayersLink(chainPlayerOne);
                    for (int i = 0; i < numberOfHeals; i++) {
                        healNearPlayers(wp, chainPlayerOne);
                    }

                    for (WarlordsEntity chainPlayerTwo : PlayerFilter
                            .entitiesAround(chainPlayerOne, bounceRange, bounceRange, bounceRange)
                            .aliveEnemiesOf(wp)
                            .excluding(nearPlayer, chainPlayerOne)
                            .soulBindedFirst(wp)
                    ) {
                        playersHit++;
                        if (chainPlayerTwo.onHorse()) {
                            numberOfDismounts++;
                        }
                        chain(chainPlayerOne.getLocation(), chainPlayerTwo.getLocation());
                        chainPlayerTwo.addDamageInstance(wp, name, minDamageHeal * .6f, maxDamageHeal * .6f, critChance, critMultiplier, false);
                        hitCounter.add(chainPlayerTwo);

                        numberOfHeals = wp.getCooldownManager().getNumberOfBoundPlayersLink(chainPlayerTwo);
                        for (int i = 0; i < numberOfHeals; i++) {
                            healNearPlayers(wp, chainPlayerTwo);
                        }

                        if (pveUpgrade) {
                            for (WarlordsEntity chainPlayerThree : PlayerFilter
                                    .entitiesAround(chainPlayerOne, bounceRange, bounceRange, bounceRange)
                                    .aliveEnemiesOf(wp)
                                    .excluding(nearPlayer, chainPlayerTwo)
                                    .soulBindedFirst(wp)
                            ) {
                                playersHit++;
                                if (chainPlayerTwo.onHorse()) {
                                    numberOfDismounts++;
                                }
                                chain(chainPlayerTwo.getLocation(), chainPlayerThree.getLocation());
                                chainPlayerThree.addDamageInstance(wp, name, minDamageHeal * .6f, maxDamageHeal * .6f, critChance, critMultiplier, false);
                                hitCounter.add(chainPlayerThree);

                                numberOfHeals = wp.getCooldownManager().getNumberOfBoundPlayersLink(chainPlayerThree);
                                for (int i = 0; i < numberOfHeals; i++) {
                                    healNearPlayers(wp, chainPlayerThree);
                                }

                                break;
                            }
                        }

                        break;
                    }
                    break;
                }
                break;
            }
        }
        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsEntity warlordsPlayer, Player player, int hitCounter) {
        player.playSound(player.getLocation(), "mage.firebreath.activation", 1, 1);

        // speed buff
        warlordsPlayer.addSpeedModifier(warlordsPlayer, "Spirit Link", 40, (int) (speedDuration * 20)); // 30 is ticks
        warlordsPlayer.getCooldownManager().addCooldown(new RegularCooldown<SpiritLink>(
                name,
                "LINK",
                SpiritLink.class,
                new SpiritLink(),
                warlordsPlayer,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                (int) (damageReductionDuration * 20)
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue = currentDamageValue * .85f;
                event.getWarlordsEntity().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });

        warlordsPlayer.setRedCurrentCooldown((float) (cooldown * warlordsPlayer.getCooldownModifier()));
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.SPRUCE_FENCE_GATE);
    }

    private void healNearPlayers(WarlordsEntity warlordsPlayer, WarlordsEntity hitPlayer) {
        //adding .25 to totem, cap 6 sec
//        new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
//                .filterName("Spirits Respite")
//                .findFirst()
//                .ifPresent(regularCooldown -> {
//                    regularCooldown.setTicksLeft(Math.min(regularCooldown.getTicksLeft() + 10, 6 * 20));
//                });
        warlordsPlayer.addHealingInstance(warlordsPlayer, "Soulbinding Weapon", 400, 400, 0, 100, false, false);
        for (WarlordsEntity nearPlayer : PlayerFilter
                .entitiesAround(warlordsPlayer, 8, 8, 8)
                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                .limit(2)
        ) {
            warlordsPlayer.doOnStaticAbility(Soulbinding.class, Soulbinding::addLinkTeammatesHealed);
            nearPlayer.addHealingInstance(warlordsPlayer, "Soulbinding Weapon", 200, 200, 0, 100, false, false);
        }
        new CooldownFilter<>(warlordsPlayer, PersistentCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .filter(soulbinding -> soulbinding.hasBoundPlayerSoul(hitPlayer))
                .forEach(soulbinding -> {
                    if (soulbinding.isPveUpgrade()) {
                        warlordsPlayer.addEnergy(warlordsPlayer, "Soulbinding Weapon", 1);
                    }
                });
    }

    public double getSpeedDuration() {
        return speedDuration;
    }

    public void setSpeedDuration(double speedDuration) {
        this.speedDuration = speedDuration;
    }

    public double getDamageReductionDuration() {
        return damageReductionDuration;
    }

    public void setDamageReductionDuration(double damageReductionDuration) {
        this.damageReductionDuration = damageReductionDuration;
    }

    public int getBounceRange() {
        return bounceRange;
    }

    public void setBounceRange(int bounceRange) {
        this.bounceRange = bounceRange;
    }
}
