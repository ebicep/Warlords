package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.AbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsSecondaryAbilityRunEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.TDAbility;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    /**
     * @return returns the input divided by 100
     */
    public static float convertToPercent(float input) {
        return input / 100f;
    }

    /**
     * @return returns the input subtracted from 100 and then divided by 100
     */
    public static float convertToDivisionDecimal(float input) {
        return (100 - input) / 100f;
    }

    /**
     * @return returns the input divided by 100 and then added on 1
     */
    public static float convertToMultiplicationDecimal(float input) {
        return 1 + (input / 100f);
    }

    //Sneak ability
    protected final List<SecondaryAbility> secondaryAbilities = new ArrayList<>();
    protected String name;
    protected int currentCharges = 1;
    protected int maxCharges = 1;
    protected float currentCooldown;
    protected FloatModifiable cooldown;
    protected FloatModifiable cooldownReductionPerTick = new FloatModifiable(.05f);
    protected FloatModifiable energyCost;
    protected TextComponent description = Component.empty();
    protected boolean boosted = false;
    //pve
    protected boolean inPve = false;
    protected boolean pveMasterUpgrade = false;
    protected boolean pveMasterUpgrade2 = false;
    private final AbstractAbilityBuilder builder;
    private boolean updateItem = true;
    private boolean initialized = false;

    public AbstractAbility(AbstractAbilityBuilder builder) {
        this.builder = builder;
    }

    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        if (!initialized) {
            try {
                throw new Exception("Ability not initialized: " + this.getClass().getSimpleName() + " - " + builder);
            } catch (Exception e) {
                ChatUtils.MessageType.GAME.sendErrorMessage(e);
            }
            init(builder);
        }
        return onActivateInternal(wp);
    }

    public void init(AbstractAbilityBuilder builder) {
        List<String> namespaces = builder.getNamespaces();
        this.name = ConfigManager.getAbilityConfigValue(namespaces, builder.getAppendedFieldName("name"), String.class);
        Float cooldownValue = builder.getCooldown() != null ?
                              builder.getCooldown() :
                              ConfigManager.getAbilityConfigValue(namespaces, builder.getAppendedFieldName("cooldown"), float.class);
        this.cooldown = new FloatModifiable(cooldownValue);
        this.currentCooldown = builder.getStartCooldown() == null ? cooldownValue : builder.getStartCooldown();
        this.energyCost = new FloatModifiable(
                builder.getEnergyCost() != null ? builder.getEnergyCost() :
                ConfigManager.getAbilityConfigValue(namespaces, builder.getAppendedFieldName("energyCost"), float.class)
        );
        if (this instanceof Damages<?> damages) {
            damages.getDamageValues().init(builder);
        }
        if (this instanceof Heals<?> heals) {
            heals.getHealValues().init(builder);
        }
        initialized = true;
    }

    public void useAbility() {
        if (getCooldownValue() == 0) {
            return;
        }
        currentCharges = Math.max(0, currentCharges - 1);
        if (currentCooldown == 0) {
            setCurrentCooldown(getCooldownValue());
        }
    }

    /**
     * @return whether the ability has to go on cooldown after activation.
     */
    protected abstract boolean onActivateInternal(@Nonnull WarlordsEntity wp);

    public AbstractAbilityBuilder getBuilder() {
        return builder;
    }

    public void updateDescription(Player player) {

    }

    public FloatModifiable getCooldownReductionPerTick() {
        return cooldownReductionPerTick;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return null;
    }

    public void boostSkill(SkillBoosts skillBoost, WarlordsEntity warlordsEntity) {
        if (!boosted) {
            boosted = true;
            skillBoost.applyBoost.accept(this);
            updateCustomStats(warlordsEntity);
        }
    }

    public void updateCustomStats(WarlordsEntity warlordsEntity) {
        getCooldown().addRefreshListener("UpdateAbilityItems", this::queueUpdateItem);
        getEnergyCost().addRefreshListener("UpdateAbilityItems", this::queueUpdateItem);
        Value.applyDamageHealing(this, value -> value.forEachAllValues(floatModifiable -> floatModifiable.addRefreshListener("UpdateAbilityItems", this::queueUpdateItem)));
    }

    public FloatModifiable getCooldown() {
        return cooldown;
    }

    public void queueUpdateItem() {
        this.updateItem = true;
    }

    public FloatModifiable getEnergyCost() {
        return energyCost;
    }

    public void addCurrentCooldown(float cooldown) {
        if (currentCharges == maxCharges) {
            currentCharges--;
        }
//        if (currentCooldown != 0) {
        currentCooldown += cooldown;
        queueUpdateItem();
//        }
    }

    public void setCurrentCharges(int currentCharges) {
        this.currentCharges = currentCharges;
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
        secondaryAbilities.add(new SecondaryAbility(runnable, infiniteUses, shouldRemove, ticksDelay));
    }

    public void runSecondAbilities(WarlordsEntity wp) {
        for (int i = 0; i < secondaryAbilities.size(); i++) {
            SecondaryAbility secondaryAbility = secondaryAbilities.get(i);
            if (secondaryAbility.getDelayTicks() > 0) {
                continue;
            }

            secondaryAbility.runnable().run();
            Bukkit.getPluginManager().callEvent(new WarlordsSecondaryAbilityRunEvent(wp, this));
            if (!secondaryAbility.hasInfiniteUses()) {
                secondaryAbilities.remove(i);
                i--;
                queueUpdateItem();
            }
        }
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
            cooldownReductionPerTick.tick();
            subtractCurrentCooldownForce(cooldownReductionPerTick.getCalculatedValue());
        }
        checkSecondaryAbilities();
        if (updateItem && warlordsEntity != null && warlordsEntity.getEntity() instanceof Player player && warlordsEntity.getGame() != null) {
            updateItem = false;
            Integer inventoryIndex = warlordsEntity.getSpec().getInventoryAbilityIndex(this);
            if (inventoryIndex == null || inventoryIndex == -1) { // exclude weapon
                return;
            }
            if (!anyCharges()) {
                ItemBuilder cooldown = new ItemBuilder(Material.GRAY_DYE, getCurrentCooldownItem());
                if (hasActiveSecondaryAbilities()) {
                    cooldown.enchant(Enchantment.RESPIRATION, 1);
                }
                player.getInventory().setItem(inventoryIndex, cooldown.get());
            } else {
                ItemStack item = getItem(this instanceof WeaponAbilityIcon ? warlordsEntity.getWeaponItem() : getAbilityIcon());
                if (getCurrentCooldown() > 0) {
                    item.setAmount(getCurrentCooldownItem());
                }
                player.getInventory().setItem(inventoryIndex, item);
            }
        }
    }

    public void setMaxCharges(int maxCharges) {
        this.maxCharges = maxCharges;
    }

    public float getCooldownValue() {
        return cooldown.getCalculatedValue();
    }

    public void subtractCurrentCooldownForce(float cooldown) {
        if (currentCooldown != 0) {
            if (currentCooldown - cooldown < 0) {
                currentCooldown = 0;
                addCharge();
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

    public void setCurrentCooldown(float currentCooldown) {
        float previousCooldown = this.currentCooldown;
        if (currentCooldown <= 0) {
            this.currentCooldown = 0;
            addCharge();
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

    public void checkSecondaryAbilities() {
        secondaryAbilities.forEach(secondaryAbility -> {
            if (secondaryAbility.getDelayTicks() > 0) {
                secondaryAbility.setDelayTicks(secondaryAbility.getDelayTicks() - 1);
            }
        });
        if (secondaryAbilities.removeIf(secondaryAbility -> secondaryAbility.shouldRemove().test(secondaryAbility))) {
            queueUpdateItem();
        }
    }

    public boolean anyCharges() {
        return currentCharges > 0;
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public int getCurrentCooldownItem() {
        return (int) Math.round(currentCooldown + .5);
    }

    public boolean hasActiveSecondaryAbilities() {
        return secondaryAbilities.stream().anyMatch(secondaryAbility -> secondaryAbility.getDelayTicks() <= 0);
    }

    public ItemStack getItem(@Nullable ItemStack item) {
        ItemBuilder itemBuilder = new ItemBuilder(item == null ? getAbilityIcon() : item)
                .name(Component.text(getName(), NamedTextColor.GREEN))
                .unbreakable();

        List<Component> lore = new ArrayList<>();
        if (this instanceof TDAbility) {

        } else {
            lore.addAll(getItemBody());
        }

        return itemBuilder.lore(lore).get();
    }

    public String getName() {
        return name;
    }

    public List<Component> getItemBody() {
        List<Component> lore = new ArrayList<>();
        lore.addAll(getItemHeader());
        lore.add(Component.empty());
        lore.addAll(getDescription());
        return lore;
    }

    public List<Component> getItemHeader() {
        List<Component> lore = new ArrayList<>();
        if (maxCharges > 1) {
            lore.add(Component.text("Max Charges: ", NamedTextColor.GRAY)
                              .append(Component.text(maxCharges, NamedTextColor.BLUE)));
        }
        if (getCooldownValue() != 0) {
            lore.add(Component.text("Cooldown: ", NamedTextColor.GRAY)
                              .append(Component.text(NumberFormat.formatOptionalTenths(getCooldownValue()) + " seconds", NamedTextColor.GOLD)));
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
                }
        );
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
        return lore;
    }

    public List<Component> getDescription() {
        return WordWrap.wrap(Component.empty().color(NamedTextColor.GRAY).append(description), DESCRIPTION_WIDTH);
    }

    public float getEnergyCostValue() {
        return energyCost.getCalculatedValue();
    }

    private void addCharge() {
        if (currentCharges >= maxCharges) {
            return;
        }
        currentCharges++;
        if (currentCharges < maxCharges) {
            currentCooldown = getCooldownValue();
        }
    }

    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
    }

    public List<SecondaryAbility> getSecondaryAbilities() {
        return secondaryAbilities;
    }

    public List<Component> getItemComponent() {
        List<Component> lore = new ArrayList<>();
        lore.add(getItemName());
        lore.add(Component.empty());

        if (this instanceof TDAbility) {

        } else {
            lore.addAll(getItemBody());
        }

        return lore;
    }

    public Component getItemName() {
        return Component.text(getName(), NamedTextColor.GREEN);
    }

    public ItemStack getItem() {
        return getItem(null);
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

    public static final class SecondaryAbility {

        private final Runnable runnable;
        private final boolean hasInfiniteUses;
        private final Predicate<SecondaryAbility> shouldRemove;
        private int delayTicks = 0;

        public SecondaryAbility(Runnable runnable, boolean hasInfiniteUses, Predicate<SecondaryAbility> shouldRemove, int delayTicks) {
            this.runnable = runnable;
            this.hasInfiniteUses = hasInfiniteUses;
            this.shouldRemove = shouldRemove;
            this.delayTicks = delayTicks;
        }

        public int getDelayTicks() {
            return delayTicks;
        }

        public void setDelayTicks(int delayTicks) {
            this.delayTicks = delayTicks;
        }

        public Runnable runnable() {
            return runnable;
        }

        public boolean hasInfiniteUses() {
            return hasInfiniteUses;
        }

        public Predicate<SecondaryAbility> shouldRemove() {
            return shouldRemove;
        }


    }

}
