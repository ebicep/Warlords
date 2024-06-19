package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.slime.SlimyChess;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SlimeSize;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class Chessking extends AbstractMob implements BossMob {

    private static final int MAX_SLIMY_CHESS = 30;

    public Chessking(Location spawnLocation) {
        this(spawnLocation, "Chessking", 50000, 0.3f, 30, 0, 0);
    }

    public Chessking(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new Belch(),
                new SpawnMobAbility(
                        20,
                        Mob.SLIME_GUARD,
                        5
                ) {
                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                },
                new SpawnMobAbility(
                        40,
                        Mob.SLIMY_CHESS,
                        15
                ) {
                    @Override
                    public int getSpawnAmount() {
                        int slimyChessCount = pveOption.getMobs().stream()
                                                       .filter(mob -> mob instanceof SlimyChess)
                                                       .mapToInt(mob -> 1)
                                                       .sum();
                        return Math.min(MAX_SLIMY_CHESS - slimyChessCount, (int) pveOption.getGame().warlordsPlayers().count());
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CHESSKING;
    }

    @Override
    public void onNPCCreate() {
        npc.getOrAddTrait(SlimeSize.class).setSize(20);
        npc.data().set(NPC.Metadata.JUMP_POWER_SUPPLIER, (Function<NPC, Float>) npc -> .1f);
    }

    @Override
    public Component getDescription() {
        return Component.text("Goblin from the local basement", NamedTextColor.GRAY);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (Utils.isProjectile(event.getCause())) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ARROW_HIT, 2, 0.1f);
            warlordsNPC.addInstance(InstanceBuilder
                    .healing()
                    .cause("Blob Heal")
                    .source(warlordsNPC)
                    .value(500)
            );
        } else {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_SLIME_ATTACK, 2, 0.2f);
        }
        SlimeSize slimeSize = npc.getOrAddTrait(SlimeSize.class);
        float healthPercent = warlordsNPC.getCurrentHealth() / warlordsNPC.getMaxHealth();
        int size = slimeSize.getSize();
        int newSize = (int) (21 * healthPercent);
        if (size != newSize && 0 < newSize && newSize < 21) {
            slimeSize.setSize(newSize);
            warlordsNPC.addSpeedModifier(warlordsNPC, "Size Change", (21 - newSize) * 7, Integer.MAX_VALUE);
            warlordsNPC.getSpeed().setChanged(true);
            float newJumpPower = 1 + ((20 - newSize) * .02f);
            npc.data().set(NPC.Metadata.JUMP_POWER_SUPPLIER, (Function<NPC, Float>) npc -> newJumpPower);
            warlordsNPC.getAbilitiesMatching(Belch.class)
                       .forEach(belch -> belch.setRange(9 - ((20 - newSize) * .2f)));
            warlordsNPC.getAbilitiesMatching(SpawnMobAbility.class)
                       .forEach(spawnMobAbility -> spawnMobAbility.getCooldown().addMultiplicativeModifierAdd("Chessking", -((20 - newSize) * .01f)));
        }
    }

    private static class Belch extends AbstractAbility implements Damages<Belch.DamageValues> {

        private float range = 9;

        public Belch() {
            super("Belch", 2800, 3600, 10, 100);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, range, range, range)
                    .aliveEnemiesOf(wp)
            ) {
                we.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.belchDamage)
                );
            }
            return true;
        }

        public float getRange() {
            return range;
        }

        public void setRange(float range) {
            this.range = range;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue belchDamage = new Value.RangedValue(2800, 3600);
            private final List<Value> values = List.of(belchDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

}
