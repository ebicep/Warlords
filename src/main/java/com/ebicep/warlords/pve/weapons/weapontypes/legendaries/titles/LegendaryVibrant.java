package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.annotation.Transient;

import java.util.*;
import java.util.stream.Collectors;

public class LegendaryVibrant extends AbstractLegendaryWeapon implements GardenOfHesperidesTitle, PassiveCounter {

    public static final int COOLDOWN = 10;
    public static final int NUMBER_OF_ORBS = 3;
    public static final ItemStack ORB_ITEM = new ItemStack(Material.SUNFLOWER);
    public static final float ORB_OFFSET = 1.75f;

    public static final int DAMAGE_TAKEN = 5;
    public static final int DAMAGE_TAKEN_PER_UPGRADE = 1;
    public static final int DAMAGE_CAP = 10_000;
    public static final int DAMAGE_CAP_PER_UPGRADE = 2_000;
    public static final int ENERGY_CAP = 500;
    public static final int ENERGY_CAP_PER_UPGRADE = 100;

    @Transient
    private int secondCounter = 0;

    public LegendaryVibrant() {
    }

    public LegendaryVibrant(UUID uuid) {
        super(uuid);
    }

    public LegendaryVibrant(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.secondCounter = 10;

        AtomicDouble damageTaken = new AtomicDouble(0);
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getTitleName(),
                null,
                LegendaryFulcrum.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 20 != 0 || secondCounter <= 0) {
                        return;
                    }
                    secondCounter--;
                    if (secondCounter == 0) {
                        secondCounter = COOLDOWN;
                        generateOrbs(player, (float) damageTaken.getAndSet(0));
                    }
                }
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                damageTaken.addAndGet(currentDamageValue);
            }
        });
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Every 10s, " + NUMBER_OF_ORBS + " energy orbs will be produced based on ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(DAMAGE_TAKEN + DAMAGE_TAKEN_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" of the damage taken for the last 10s. A max of " + NumberFormat.addCommas(DAMAGE_CAP) + " damage may be tabulated and a max of "))
                        .append(formatTitleUpgrade(ENERGY_CAP + ENERGY_CAP_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text("energy may be produced between all orbs. " +
                                "Orbs last for 30s and any not claimed by a player will be sent back to the equipped player at only 30% of the original energy amount per orb."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.VIBRANT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 165;
    }

    @Override
    protected float getHealthBonusValue() {
        return 600;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 7;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 15;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 175;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(DAMAGE_TAKEN + DAMAGE_TAKEN_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DAMAGE_TAKEN + DAMAGE_TAKEN_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(ENERGY_CAP + ENERGY_CAP_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(ENERGY_CAP + ENERGY_CAP_PER_UPGRADE * getTitleLevelUpgraded())
                )
        );
    }

    private void generateOrbs(WarlordsEntity player, float damageTaken) {
        damageTaken = Math.min(DAMAGE_CAP, damageTaken);
        if (damageTaken == 0) {
            return;
        }
        float damageTakenMultiplier = (DAMAGE_TAKEN + DAMAGE_TAKEN_PER_UPGRADE * getTitleLevel()) / 100f;
        float energyPerOrb = Math.min(ENERGY_CAP + ENERGY_CAP_PER_UPGRADE * getTitleLevel(), damageTaken * damageTakenMultiplier) / NUMBER_OF_ORBS;
        List<ItemDisplay> itemDisplays = LocationUtils
                .getCircle(LocationUtils.getGroundLocation(player.getLocation()), 3, NUMBER_OF_ORBS)
                .stream()
                .map(location -> player.getWorld().spawn(
                        location.clone().add(0, ORB_OFFSET, 0),
                        ItemDisplay.class,
                        false,
                        itemDisplay -> itemDisplay.setItemStack(ORB_ITEM)
                ))
                .collect(Collectors.toList());
        new GameRunnable(player.getGame()) {
            int ticksElapsed = 0;

            @Override
            public void run() {
                for (int i = 0; i < itemDisplays.size(); i++) {
                    ItemDisplay itemDisplay = itemDisplays.get(i);
                    itemDisplay.setRotation(itemDisplay.getLocation().getYaw() + 10, 0);
                    Location orbLocation = itemDisplay.getLocation();
                    Optional<WarlordsEntity> optionalWarlordsEntity = PlayerFilter
                            .entitiesAround(orbLocation.clone().subtract(0, ORB_OFFSET, 0), 1.35, 1.35, 1.35)
                            .aliveTeammatesOf(player)
                            .findFirst();
                    if (optionalWarlordsEntity.isEmpty()) {
                        continue;
                    }
                    WarlordsEntity warlordsEntity = optionalWarlordsEntity.get();
                    warlordsEntity.playSound(warlordsEntity.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                    warlordsEntity.addEnergy(player, "Energy Orb", energyPerOrb);
                    itemDisplay.remove();
                    itemDisplays.remove(i);
                    i--;
                }
                if (ticksElapsed++ >= 30 * 20 || itemDisplays.isEmpty()) {
                    for (ItemDisplay itemDisplay : itemDisplays) {
                        EffectUtils.playParticleLinkAnimation(itemDisplay.getLocation(), player.getLocation(), 255, 255, 0, 1);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                        player.addEnergy(player, "Energy Orb", energyPerOrb * 0.3f);
                        itemDisplay.remove();
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public int getCounter() {
        return secondCounter;
    }
}
