package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.icon.AbilityIcon;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.towerdefense.towers.TDAbility;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractAbility implements AbilityIcon {

    protected static final int DESCRIPTION_WIDTH = 165;

    protected static void playCooldownReductionEffect(WarlordsEntity warlordsEntity) {
        new GameRunnable(warlordsEntity.getGame()) {
            @Override
            public void run() {
                EffectUtils.displayParticle(
                        Particle.BUBBLE_POP,
                        warlordsEntity.getLocation().add(0, 1.5, 0),
                        10,
                        .5,
                        .25,
                        .5,
                        0
                );
            }
        }.runTaskLater(2);

    }

    public static Component formatRange(float min, float max, NamedTextColor textColor) {
        return Component.text(" ", NamedTextColor.GRAY)
                        .append(Component.text(format(min), textColor))
                        .append(Component.text(" - "))
                        .append(Component.text(format(max), textColor))
                        .append(Component.text(" "));
    }

    public static String format(double input) {
        return NumberFormat.formatOptionalTenths(input);
    }

    public static String formatHundredths(double input) {
        return NumberFormat.formatOptionalHundredths(input);
    }

    //Sneak ability
    protected final List<SecondaryAbility> secondaryAbilities = new ArrayList<>();
    protected int timesUsed = 0;
    protected String name;
    protected float currentCooldown;
    protected FloatModifiable cooldown;
    protected FloatModifiable energyCost;
    protected TextComponent description = Component.empty();
    protected boolean boosted = false;
    //pve
    protected boolean inPve = false;
    protected boolean pveMasterUpgrade = false;
    protected boolean pveMasterUpgrade2 = false;
    private boolean updateItem = true;

    public AbstractAbility(String name, float cooldown, float energyCost, float startCooldown) {
        this.name = name;
        this.cooldown = new FloatModifiable(cooldown);
        this.currentCooldown = startCooldown;
        this.energyCost = new FloatModifiable(energyCost);
    }

    public AbstractAbility(String name, float cooldown, float energyCost, boolean startNoCooldown) {
        this(name, cooldown, energyCost, startNoCooldown ? 0 : cooldown);
    }

    public AbstractAbility(String name, float cooldown, float energyCost) {
        this(name, cooldown, energyCost, 0);
    }

    public void updateDescription(Player player) {

    }

    @Nullable
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    /**
     * @return whether the ability has to go on cooldown after activation.
     */
    public abstract boolean onActivate(@Nonnull WarlordsEntity wp);

    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return null;
    }

    public void boostSkill(SkillBoosts skillBoost, AbstractPlayerClass abstractPlayerClass) {
        if (!boosted) {
            boosted = true;
            skillBoost.applyBoost.accept(this);
            updateCustomStats(abstractPlayerClass);
        }
    }

    public void updateCustomStats(AbstractPlayerClass apc) {
        Value.applyDamageHealing(this, value -> value.forEachAllValues(floatModifiable -> floatModifiable.addRefreshListener("UpdateAbilityItems", this::queueUpdateItem)));
    }

    public void addTimesUsed() {
        this.timesUsed++;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public void addCurrentCooldown(float cooldown) {
        if (currentCooldown != 0) {
            currentCooldown += cooldown;
            queueUpdateItem();
        }
    }

    public void queueUpdateItem() {
        this.updateItem = true;
    }

    /**
     * @param ticksDelay   how many ticks before it allows you to activate the ability
     * @param runnable     secondary ability runnable
     * @param infiniteUses should the ability have infinite uses
     * @param shouldRemove remove condition
     */
    public void addSecondaryAbility(
            int ticksDelay,
            Runnable runnable,
            boolean infiniteUses,
            Predicate<SecondaryAbility> shouldRemove
    ) {
        new BukkitRunnable() {
            @Override
            public void run() {
                secondaryAbilities.add(new SecondaryAbility(runnable, infiniteUses, shouldRemove));
            }
        }.runTaskLater(Warlords.getInstance(), ticksDelay);
    }

    public void runSecondAbilities() {
        for (int i = 0; i < secondaryAbilities.size(); i++) {
            SecondaryAbility secondaryAbility = secondaryAbilities.get(i);

            secondaryAbility.runnable().run();
            if (!secondaryAbility.hasInfiniteUses()) {
                secondaryAbilities.remove(i);
                i--;
                queueUpdateItem();
            }
        }
    }

    public FloatModifiable getCooldown() {
        return cooldown;
    }

    public FloatModifiable getEnergyCost() {
        return energyCost;
    }

    /**
     * @return returns the input divided by 100
     */
    public static float convertToPercent(float input) {
        return input / 100f;
    }

    /**
     * @return returns the input subtracted from 100 and then divided by 100
     */
    public float convertToDivisionDecimal(float input) {
        return (100 - input) / 100f;
    }

    /**
     * @return returns the input divided by 100 and then added on 1
     */
    public float convertToMultiplicationDecimal(float input) {
        return 1 + (input / 100f);
    }

    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
    }

    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        Value.applyDamageHealing(this, value -> value.forEachAllValues(FloatModifiable::tick));
        cooldown.tick();
        energyCost.tick();
        if (this instanceof HitBox hitBox) {
            hitBox.getHitBoxRadius().tick();
        }
        if (this instanceof Splash splash) {
            splash.getSplashRadius().tick();
        }
        if (getCooldownValue() > 0) {
            subtractCurrentCooldownForce(.05f);
        }
        checkSecondaryAbilities();
        if (updateItem && warlordsEntity != null && warlordsEntity.getEntity() instanceof Player player) {
            updateItem = false;
            Integer inventoryIndex = warlordsEntity.getSpec().getInventoryAbilityIndex(this);
            if (inventoryIndex == null) { // exclude weapon
                return;
            }
            if (getCurrentCooldown() > 0) {
                ItemBuilder cooldown = new ItemBuilder(Material.GRAY_DYE, getCurrentCooldownItem());
                if (!getSecondaryAbilities().isEmpty()) {
                    cooldown.enchant(Enchantment.OXYGEN, 1);
                }
                player.getInventory().setItem(inventoryIndex, cooldown.get());
            } else {
                player.getInventory().setItem(inventoryIndex, getItem());
            }
        }
    }

    public float getCooldownValue() {
        return cooldown.getCalculatedValue();
    }

    public void subtractCurrentCooldownForce(float cooldown) {
        if (currentCooldown != 0) {
            if (currentCooldown - cooldown < 0) {
                currentCooldown = 0;
                queueUpdateItem();
            } else {
                int previousCooldown = (int) currentCooldown;
                currentCooldown -= cooldown;
                if (previousCooldown != (int) currentCooldown) { // only update if second changed
                    queueUpdateItem();
                }
            }
        }
    }

    public void checkSecondaryAbilities() {
        if (secondaryAbilities.removeIf(secondaryAbility -> secondaryAbility.shouldRemove().test(secondaryAbility))) {
            queueUpdateItem();
        }
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public int getCurrentCooldownItem() {
        return (int) Math.round(currentCooldown + .5);
    }

    public List<SecondaryAbility> getSecondaryAbilities() {
        return secondaryAbilities;
    }

    public ItemStack getItem() {
        return getItem(getAbilityIcon());
    }

    public ItemStack getItem(ItemStack item) {
        ItemBuilder itemBuilder = new ItemBuilder(item)
                .name(Component.text(getName(), NamedTextColor.GOLD))
                .unbreakable();

        List<Component> lore = new ArrayList<>();
        if (this instanceof TDAbility) {

        } else {
            if (getCooldownValue() != 0) {
                lore.add(Component.text("Cooldown: ", NamedTextColor.GRAY)
                                  .append(Component.text(NumberFormat.formatOptionalTenths(getCooldownValue()) + " seconds", NamedTextColor.AQUA)));
            }
            if (getEnergyCostValue() != 0) {
                lore.add(Component.text("Energy Cost: ", NamedTextColor.GRAY)
                                  .append(Component.text(NumberFormat.formatOptionalTenths(getEnergyCostValue()), NamedTextColor.YELLOW)));
            }
            List<Component> critChanceLore = new ArrayList<>();
            List<Component> critMultiplierLore = new ArrayList<>();
            Value.applyDamageHealing(this, (damage, value) -> {
                if (value instanceof Value.RangedValueCritable critable) {
                    TextColor textColor = damage ? NamedTextColor.RED : NamedTextColor.GREEN;
                    critChanceLore.add(Component.text(format(Math.min(critable.critChance().getCalculatedValue(), 100)) + "%", textColor));
                    critMultiplierLore.add(Component.text(format(critable.critMultiplier().getCalculatedValue()) + "%", textColor));
                }
            });
            if (!critChanceLore.isEmpty()) {
                lore.add(ComponentBuilder
                        .create("Crit Chance: ", NamedTextColor.GRAY)
                        .append(critChanceLore.stream().collect(Component.toComponent(Component.text("/", NamedTextColor.GRAY))))
                        .build());
                lore.add(ComponentBuilder
                        .create("Crit Multiplier: ", NamedTextColor.GRAY)
                        .append(critMultiplierLore.stream().collect(Component.toComponent(Component.text("/", NamedTextColor.GRAY))))
                        .build());
            }
            lore.add(Component.empty());
            lore.addAll(getDescription());
        }

        return itemBuilder.lore(lore).get();
    }

    public String getName() {
        return name;
    }

    public float getEnergyCostValue() {
        return energyCost.getCalculatedValue();
    }

    public List<Component> getDescription() {
        return WordWrap.wrap(Component.empty().color(NamedTextColor.GRAY).append(description), DESCRIPTION_WIDTH);
    }

    public void setCurrentCooldown(float currentCooldown) {
        float previousCooldown = this.currentCooldown;
        if (currentCooldown < 0) {
            this.currentCooldown = 0;
            if (previousCooldown > 0) { // only update if it was on cooldown
                queueUpdateItem();
            }
        } else {
            this.currentCooldown = currentCooldown;
            if ((int) previousCooldown != (int) currentCooldown) { // only update if second changed
                queueUpdateItem();
            }
        }
    }

    public void subtractCurrentCooldown(float cooldown) {
        if (inPve && this instanceof CanReduceCooldowns canReduceCooldowns && canReduceCooldowns.canReduceCooldowns()) {
            return;
        }
        subtractCurrentCooldownForce(cooldown);
    }

    public boolean isInPve() {
        return inPve;
    }

    public void setInPve(boolean inPve) {
        this.inPve = inPve;
        queueUpdateItem();
    }

    public boolean isPveMasterUpgrade() {
        return pveMasterUpgrade;
    }

    public void setPveMasterUpgrade(boolean pveMasterUpgrade) {
        this.pveMasterUpgrade = pveMasterUpgrade;
    }

    public boolean isPveMasterUpgrade2() {
        return pveMasterUpgrade2;
    }

    public void setPveMasterUpgrade2(boolean pveMasterUpgrade2) {
        this.pveMasterUpgrade2 = pveMasterUpgrade2;
    }

    public boolean isUpdateItem() {
        return updateItem;
    }

    public record SecondaryAbility(Runnable runnable, boolean hasInfiniteUses, Predicate<SecondaryAbility> shouldRemove) {

    }
}
