package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.SoulbindingWeaponBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Soulbinding extends AbstractAbility implements PurpleAbilityIcon, Duration, CanReduceCooldowns, Heals<Soulbinding.HealingValues>, AbilityStats<Soulbinding, Soulbinding.SoulbindingStats> {

    private final SoulbindingStats stats = new SoulbindingStats();
    private final HealingValues healingValues = new HealingValues();
    private int tickDuration = 240;
    private float selfCooldownReduction = 1.5f;
    private float allyCooldownReduction = .75f;
    private int bindDuration = 60;
    private int radius = 8;
    private int maxAlliesHit = 2;
    private int maxStacks = 1;
    private int kbRes = 10;

    public Soulbinding() {
        super(AbstractAbilityBuilder.create("soulbindingWeapon").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.selfCooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("selfCooldownReduction"), float.class);
        this.allyCooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("allyCooldownReduction"), float.class);
        this.bindDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("bindDuration"), int.class);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), int.class);
        this.maxAlliesHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAlliesHit"), int.class);
        this.maxStacks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxStacks"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        activeSoulbinding(wp);
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Your melee attacks give enemies ")
                                               .text("BOUND", NamedTextColor.LIGHT_PURPLE)
                                               .text(" for ")
                                               .durationTicks(bindDuration)
                                               .text(". Against ")
                                               .text("BOUND", NamedTextColor.LIGHT_PURPLE)
                                               .text(" targets, your next Spirit Link will heal you for ")
                                               .heal(healingValues.selfHealing)
                                               .text(" health and ")
                                               .text(maxAlliesHit, NamedTextColor.BLUE)
                                               .text(" nearby allies for ")
                                               .heal(healingValues.allyHealing)
                                               .text(". Your next Fallen Souls will reduce the cooldown of all abilities by ")
                                               .durationSeconds(selfCooldownReduction)
                                               .text(". (")
                                               .durationSeconds(allyCooldownReduction)
                                               .text(" for ")
                                               .text(maxAlliesHit, NamedTextColor.BLUE)
                                               .text(" nearby allies). Both buffs may be activated for every melee hit. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .emptyLine()
                                               .text("Successful soulbind procs with Spirit Link will grant you ")
                                               .percent(kbRes, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" knockback resistance for ")
                                               .durationSeconds(1.2f)
                                               .text(" (Max ")
                                               .durationSeconds(3.6f)
                                               .text(").")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulbindingWeaponBranch(abilityTree, this);
    }

    public SoulbindingData activeSoulbinding(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.consecrate.activation", 2, 2);
        wp.getCooldownManager().limitCooldowns(PersistentCooldown.class, Soulbinding.SoulbindingData.class, wp.isInPve() ? 2 : maxStacks);
        SoulbindingData data = new SoulbindingData(this);
        PersistentCooldown<SoulbindingData> soulBindingCooldown = new PersistentCooldown<>(
                name,
                "SOUL",
                SoulbindingData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    data.markAllBoundNamesDirty();
                    if (new CooldownFilter<>(cooldownManager, PersistentCooldown.class).filterCooldownClass(SoulbindingData.class).stream().count() == 1) {
                        if (wp.getEntity() instanceof Player) {
                            ItemStack item = ((Player) wp.getEntity()).getInventory().getItem(0);
                            if (item != null) {
                                item.removeEnchantment(Enchantment.RESPIRATION);
                            }
                        }
                    }
                },
                tickDuration,
                soulbinding -> soulbinding.getSoulBindedPlayers().isEmpty(),
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed == 1) {
                        this.energyCost.addModifier(FloatModifiable.ModifierType.OVERRIDING, "Soulbinding Reactivation", 0, tickDuration);
                    }
                    if (ticksElapsed % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.2, 0);
                        EffectUtils.displayParticle(Particle.WITCH, location, 2, 0.2, 0, 0.2, 0.1);
                    }
                    data.tickSoulBoundPlayers(wp);
                })
        ) {
            @Override
            public PlayerNameData addSuffixFromSelf() {
                return new PlayerNameData(Component.text("BOUND", NamedTextColor.LIGHT_PURPLE),
                        we -> data.getSoulBindedPlayers().stream().anyMatch(soulBoundPlayer -> soulBoundPlayer.getBoundPlayer() == we)
                );
            }
        };
        soulBindingCooldown.addModifier(Modifier.ON_OUTGOING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                    if (!event.getCause().isEmpty()) {
                        return;
                    }
                    WarlordsEntity wpAttacker = event.getSource();
                    WarlordsEntity wpVictim = event.getWarlordsEntity();
                    if (!event.getCause().isEmpty() || wpAttacker == wpVictim) {
                        return;
                    }
                    if (!soulBindingCooldown.hasTicksLeft()) {
                        return;
                    }
                    data.bindPlayer(wpAttacker, wpVictim);
                }
        );
        wp.getCooldownManager().addCooldown(soulBindingCooldown);
        if (wp.getEntity() instanceof Player player) {
            ItemStack item = player.getInventory().getItem(0);
            if (item != null) {
                ItemMeta newItemMeta = item.getItemMeta();
                newItemMeta.addEnchant(Enchantment.RESPIRATION, 1, true);
                item.setItemMeta(newItemMeta);
            }
        }
        return data;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public SoulbindingStats getAbilityStats() {
        return stats;
    }

    public int getKbRes() {
        return kbRes;
    }

    public void setKbRes(int kbRes) {
        this.kbRes = kbRes;
    }

    public void addPlayersBinded() {
        stats.playersBinded++;
    }

    public void addSoulProcs() {
        stats.soulProcs++;
    }

    public void addLinkProcs() {
        stats.linkProcs++;
    }

    public void addSoulTeammatesCDReductions() {
        stats.soulTeammatesCDReductions++;
    }

    public void addLinkTeammatesHealed() {
        stats.linkTeammatesHealed++;
    }

    public int getBindDuration() {
        return bindDuration;
    }

    public void setBindDuration(int bindDuration) {
        this.bindDuration = bindDuration;
    }

    public float getSelfCooldownReduction() {
        return selfCooldownReduction;
    }

    public void setSelfCooldownReduction(float selfCooldownReduction) {
        this.selfCooldownReduction = selfCooldownReduction;
    }

    public float getAllyCooldownReduction() {
        return allyCooldownReduction;
    }

    public void setAllyCooldownReduction(float allyCooldownReduction) {
        this.allyCooldownReduction = allyCooldownReduction;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMaxAlliesHit() {
        return maxAlliesHit;
    }

    public void setMaxAlliesHit(int maxAlliesHit) {
        this.maxAlliesHit = maxAlliesHit;
    }

    private void triggerChainOfCustody(WarlordsEntity owner, SoulbindingData data, SoulBoundPlayer soulBoundPlayer) {
        if (!pveMasterUpgrade2) {
            return;
        }

        if (soulBoundPlayer.getCustodyJumps() >= 3) {
            return;
        }

        WarlordsEntity fallenEnemy = soulBoundPlayer.getBoundPlayer();
        Location origin = fallenEnemy.getDeathLocation() != null ? fallenEnemy.getDeathLocation() : fallenEnemy.getLocation();
        WarlordsEntity nextTarget = findChainOfCustodyTarget(owner, data, fallenEnemy, origin);

        if (nextTarget == null) {
            return;
        }

        data.bindPlayerFromCustody(owner, nextTarget, soulBoundPlayer.getCustodyJumps() + 1);
        releaseCustodyWave(owner, origin);
    }

    private WarlordsEntity findChainOfCustodyTarget(WarlordsEntity owner, SoulbindingData data, WarlordsEntity fallenEnemy, Location origin) {
        return PlayerFilter.entitiesAround(origin, 6, 6, 6)
                .aliveEnemiesOf(owner)
                .excluding(fallenEnemy)
                .filter(target -> !data.hasBoundPlayer(target))
                .closestFirst(origin)
                .findFirst()
                .orElse(null);
    }

    private void releaseCustodyWave(WarlordsEntity owner, Location origin) {
        Utils.playGlobalSound(origin, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 2, .7f);

        origin.getWorld().spawnParticle(
                Particle.WITCH,
                origin.clone().add(0, 1.1, 0),
                32,
                .7,
                .5,
                .7,
                .05
        );

        origin.getWorld().spawnParticle(
                Particle.SOUL_FIRE_FLAME,
                origin.clone().add(0, .8, 0),
                28,
                .7,
                .35,
                .7,
                .04
        );

        PlayerFilter.entitiesAround(origin, 6, 6, 6)
                .aliveTeammatesOf(owner)
                .forEach(this::reduceCustodyAllyCooldowns);

        PlayerFilter.entitiesAround(origin, 6, 6, 6)
                .aliveEnemiesOf(owner)
                .forEach(enemy -> enemy.addSpeedModifier(owner, "Chain of Custody", 25, 3 * 20));
    }

    private void reduceCustodyAllyCooldowns(WarlordsEntity ally) {
        ally.getAbilities().forEach(ability -> {
            if (ability.getCurrentCooldown() > 0) {
                ability.subtractCurrentCooldownForce(0.1f);
                playCooldownReductionEffect(ally);
            }
        });
    }

    public static class SoulbindingData {

        private final Soulbinding soulbinding;

        private final List<SoulBoundPlayer> soulBindedPlayers = new ArrayList<>();

        private final List<WarlordsEntity> playersProcedBySouls = new ArrayList<>();

        private final List<WarlordsEntity> playersProcedByLink = new ArrayList<>();

        public SoulbindingData(Soulbinding soulbinding) {
            this.soulbinding = soulbinding;
        }

        public void tickSoulBoundPlayers(WarlordsEntity owner) {
            List<SoulBoundPlayer> custodyTransfers = new ArrayList<>();
            Iterator<SoulBoundPlayer> iterator = soulBindedPlayers.iterator();

            while (iterator.hasNext()) {
                SoulBoundPlayer soulBoundPlayer = iterator.next();
                WarlordsEntity boundPlayer = soulBoundPlayer.getBoundPlayer();

                soulBoundPlayer.decrementTimeLeft();

                if (boundPlayer.isDead()) {
                    iterator.remove();
                    markBoundNameDirty(boundPlayer);
                    custodyTransfers.add(soulBoundPlayer);
                    continue;
                }

                if (soulBoundPlayer.getTimeLeft() == 0 || soulBoundPlayer.isHitWithSoul() && soulBoundPlayer.isHitWithLink()) {
                    iterator.remove();
                    markBoundNameDirty(boundPlayer);
                }
            }

            for (SoulBoundPlayer soulBoundPlayer : custodyTransfers) {
                soulbinding.triggerChainOfCustody(owner, this, soulBoundPlayer);
            }
        }

        public void bindPlayerFromCustody(WarlordsEntity wpAttacker, WarlordsEntity wpVictim, int custodyJumps) {
            soulbinding.addPlayersBinded();

            if (hasBoundPlayer(wpVictim)) {
                getSoulBindedPlayers().stream().filter(p -> p.getBoundPlayer() == wpVictim).forEach(boundPlayer -> {
                    boundPlayer.setHitWithSoul(false);
                    boundPlayer.setHitWithLink(false);
                    boundPlayer.setTicksLeft(soulbinding.bindDuration);
                    boundPlayer.setCustodyJumps(custodyJumps);
                });
                return;
            }

            wpVictim.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" You have been chained by " + wpAttacker.getName() + "'s ", NamedTextColor.GRAY))
                    .append(Component.text("Chain of Custody", NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("!", NamedTextColor.GRAY)));

            wpAttacker.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text("Chain of Custody", NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" has bound " + wpVictim.getName() + "!", NamedTextColor.GRAY)));

            getSoulBindedPlayers().add(new SoulBoundPlayer(wpVictim, soulbinding.bindDuration, custodyJumps));
            markBoundNameDirty(wpVictim);
            Utils.playGlobalSound(wpVictim.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
        }

        public void bindPlayer(WarlordsEntity wpAttacker, WarlordsEntity wpVictim) {
            soulbinding.addPlayersBinded();
            if (hasBoundPlayer(wpVictim)) {
                getSoulBindedPlayers().stream().filter(p -> p.getBoundPlayer() == wpVictim).forEach(boundPlayer -> {
                    boundPlayer.setHitWithSoul(false);
                    boundPlayer.setHitWithLink(false);
                    boundPlayer.setTicksLeft(soulbinding.bindDuration);
                });
            } else {
                wpVictim.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" You have been bound by " + wpAttacker.getName() + "'s ", NamedTextColor.GRAY))
                                                                     .append(Component.text("Soulbinding Weapon", NamedTextColor.LIGHT_PURPLE))
                                                                     .append(Component.text("!", NamedTextColor.GRAY)));
                wpAttacker.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your ", NamedTextColor.GRAY))
                                                                      .append(Component.text("Soulbinding Weapon", NamedTextColor.LIGHT_PURPLE))
                                                                      .append(Component.text(" has bound " + wpVictim.getName() + "!", NamedTextColor.GRAY)));
                getSoulBindedPlayers().add(new SoulBoundPlayer(wpVictim, soulbinding.bindDuration));
                markBoundNameDirty(wpVictim);
                Utils.playGlobalSound(wpVictim.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
            }
        }

        private void markBoundNameDirty(WarlordsEntity victim) {
            victim.getCooldownManager().markNameDisplayDirty();
        }

        private void markAllBoundNamesDirty() {
            for (SoulBoundPlayer soulBoundPlayer : List.copyOf(soulBindedPlayers)) {
                markBoundNameDirty(soulBoundPlayer.getBoundPlayer());
            }
        }

        public boolean hasBoundPlayer(WarlordsEntity warlordsPlayer) {
            for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
                if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                    return true;
                }
            }
            return false;
        }

        public List<SoulBoundPlayer> getSoulBindedPlayers() {
            return soulBindedPlayers;
        }

        public boolean hasBoundPlayerSoul(WarlordsEntity warlordsPlayer) {
            for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
                if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                    if (!soulBindedPlayer.isHitWithSoul()) {
                        soulBindedPlayer.setHitWithSoul(true);
                        playersProcedBySouls.add(warlordsPlayer);
                        soulbinding.addSoulProcs();
                        return true;
                    }
                    break;
                }
            }
            return false;
        }

        public boolean hasBoundPlayerLink(WarlordsEntity warlordsPlayer) {
            for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
                if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                    if (!soulBindedPlayer.isHitWithLink()) {
                        soulBindedPlayer.setHitWithLink(true);
                        playersProcedByLink.add(warlordsPlayer);
                        soulbinding.addLinkProcs();
                        return true;
                    }
                    break;
                }
            }
            return false;
        }

        public List<WarlordsEntity> getAllProcedPlayers() {
            List<WarlordsEntity> procedPlayers = new ArrayList<>();
            procedPlayers.addAll(playersProcedBySouls);
            procedPlayers.addAll(playersProcedByLink);
            return procedPlayers;
        }

        public Soulbinding getSoulbinding() {
            return soulbinding;
        }

        public List<WarlordsEntity> getPlayersProcedBySouls() {
            return playersProcedBySouls;
        }

        public List<WarlordsEntity> getPlayersProcedByLink() {
            return playersProcedByLink;
        }

    }

    public static class SoulBoundPlayer {

        private WarlordsEntity boundPlayer;

        private int ticksLeft;

        private boolean hitWithLink;

        private boolean hitWithSoul;

        private int custodyJumps;

        public SoulBoundPlayer(WarlordsEntity boundPlayer, int timeLeft) {
            this(boundPlayer, timeLeft, 0);
        }

        // pve
        public SoulBoundPlayer(WarlordsEntity boundPlayer, int timeLeft, int custodyJumps) {
            this.boundPlayer = boundPlayer;
            this.ticksLeft = timeLeft;
            this.custodyJumps = custodyJumps;
        }

        public int getCustodyJumps() {
            return custodyJumps;
        }

        public void setCustodyJumps(int custodyJumps) {
            this.custodyJumps = custodyJumps;
        }

        public WarlordsEntity getBoundPlayer() {
            return boundPlayer;
        }

        public void setBoundPlayer(WarlordsEntity boundPlayer) {
            this.boundPlayer = boundPlayer;
        }

        public float getTimeLeft() {
            return ticksLeft;
        }

        public void setTicksLeft(int timeLeft) {
            this.ticksLeft = timeLeft;
        }

        public void decrementTimeLeft() {
            this.ticksLeft--;
        }

        public boolean isHitWithLink() {
            return hitWithLink;
        }

        public void setHitWithLink(boolean hitWithLink) {
            this.hitWithLink = hitWithLink;
        }

        public boolean isHitWithSoul() {
            return hitWithSoul;
        }

        public void setHitWithSoul(boolean hitWithSoul) {
            this.hitWithSoul = hitWithSoul;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue allyHealing = new Value.SetValue(200);

        private Value.SetValue selfHealing = new Value.SetValue(300);

        private List<Value> values = List.of(allyHealing, selfHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.allyHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("allyHealing"), Value.SetValue.class);
            this.selfHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("selfHealing"), Value.SetValue.class);
            this.values = List.of(allyHealing, selfHealing);
        }

        public Value.SetValue getAllyHealing() {
            return allyHealing;
        }

        public Value.SetValue getSelfHealing() {
            return selfHealing;
        }

    }

    public static class SoulbindingStats extends AbstractAbilityStats<Soulbinding, SoulbindingStats> {

        @Field("targets_binded")
        private int playersBinded = 0;

        @Field("soul_procs")
        private int soulProcs = 0;

        @Field("link_procs")
        private int linkProcs = 0;

        @Field("soul_teammates_cd_reductions")
        private int soulTeammatesCDReductions = 0;

        @Field("link_teammates_healed")
        private int linkTeammatesHealed = 0;

        @Override
        public Class<SoulbindingStats> getClazz() {
            return SoulbindingStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Binded", playersBinded));
            statsDisplay.add(new AbilityStatDisplay("Soul Procs", soulProcs));
            statsDisplay.add(new AbilityStatDisplay("Soul Teammates CD Reductions", soulTeammatesCDReductions));
            statsDisplay.add(new AbilityStatDisplay("Link Procs", linkProcs));
            statsDisplay.add(new AbilityStatDisplay("Link Teammates Healed", linkTeammatesHealed));
            return statsDisplay;
        }

        @Override
        public SoulbindingStats merge(SoulbindingStats other, int multiplier) {
            SoulbindingStats stats = super.merge(other, multiplier);
            stats.playersBinded = this.playersBinded + other.playersBinded * multiplier;
            stats.soulProcs = this.soulProcs + other.soulProcs * multiplier;
            stats.linkProcs = this.linkProcs + other.linkProcs * multiplier;
            stats.soulTeammatesCDReductions = this.soulTeammatesCDReductions + other.soulTeammatesCDReductions * multiplier;
            stats.linkTeammatesHealed = this.linkTeammatesHealed + other.linkTeammatesHealed * multiplier;
            return stats;
        }

        @Override
        public SoulbindingStats create() {
            return new SoulbindingStats();
        }

    }

}
