package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.mage.specs.aquamancer.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.shaman.specs.earthwarden.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.thunderlord.ThunderLord;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.ClassesSkillBoosts.*;

public enum Classes {
    PYROMANCER("Pyromancer",
            Pyromancer::new,
            "§7A damage-oriented Mage specialization that uses the destructive Fire spells to obliterate enemies.",
            new ItemStack(Material.NETHER_STALK, 1),
            FIREBALL, FLAME_BURST),
    CRYOMANCER("Cryomancer",
            Cryomancer::new,
            "§7A defense-oriented Mage specialization that uses Ice spells to slow down enemies and to creative defensive barriers.",
            new ItemStack(Material.CLAY_BALL, 1),
            FROST_BOLT, FREEZING_BREATH),
    AQUAMANCER("Aquamancer",
            Aquamancer::new,
            "§7A healing-oriented Mage specialization that uses Water spells to heal allies and to deal minor damage to enemies.",
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            WATER_BOLT, WATER_BREATH),
    BERSERKER("Berserker",
            Berserker::new,
            "§7A damage-oriented Warrior specialization with a lust for blood and anger issues.",
            new ItemStack(Material.NETHER_STALK, 1),
            WOUNDING_STRKE, SEISMIC_WAVE, GROUND_SLAM),
    DEFENDER("Defender",
            Defender::new,
            "§7A defense-oriented Warrior specialization that can protect teammates by mitigating damage and intercepting enemy hits.",
            new ItemStack(Material.CLAY_BALL, 1),
            WOUNDING_STRKE, SEISMIC_WAVE, GROUND_SLAM),
    REVENANT("Revenant",
            Revenant::new,
            "§7A support-oriented Warrior specialization that can give allies a second chance of life.",
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            CRIPPLING_STRIKE, ORBS_OF_LIFE),
    AVENGER("Avenger",
            Avenger::new,
            "§7A damage-oriented Paladin specialization that focuses on draining energy from enemies and has access to minor healing.",
            new ItemStack(Material.NETHER_STALK, 1),
            AVENGER_STRIKE, CONSECRATE),
    CRUSADER("Crusader",
            Crusader::new,
            "§7A defense-oriented Paladin specialization that inspires allies by granting them more energy in battle and has access to minor healing.",
            new ItemStack(Material.CLAY_BALL, 1),
            CRUSADER_STRIKE, CONSECRATE),
    PROTECTOR("Protector",
            Protector::new,
            "§7A healing-oriented Paladin specialization that converts damage into healing for his allies and has access to greater healing abilities.",
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            PROTECTOR_STRIKE, HOLY_RADIANCE, HAMMER_OF_LIGHT),
    THUNDERLORD("Thunderlord",
            ThunderLord::new,
            "§7A damage-oriented Shaman specialization that calls upon the power of Lightning to electrocute enemies.",
            new ItemStack(Material.NETHER_STALK, 1),
            LIGHTNING_BOLT, CHAIN_LIGHTNING, WINDFURY_WEAPON),
    SPIRITGUARD("Spiritguard",
            Spiritguard::new,
            "§7A defense-oriented Shaman specialization that calls upon the aid of spirits old and new to mitigate damage and avoid death.",
            new ItemStack(Material.CLAY_BALL, 1),
            FALLEN_SOULS, SPIRIT_LINK),
    EARTHWARDEN("Earthwarden",
            Earthwarden::new,
            "§7A healing-oriented Shaman specialization that calls upon the power of Earth to crush enemies and to aid allies.",
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            EARTHEN_SPIKE, BOULDER, CHAIN_HEAL),

    ;

    public final String name;
    public final Function<Player, PlayerClass> create;
    public final String description;
    public final ItemStack icon;
    public final List<ClassesSkillBoosts> skillBoosts;

    Classes(String name, Function<Player, PlayerClass> create, String description, ItemStack icon, ClassesSkillBoosts... skillBoosts) {
        this.name = name;
        this.create = create;
        this.description = description;
        this.icon = icon;
        this.skillBoosts = Arrays.asList(skillBoosts);
    }

    public static ClassesGroup getClassesGroup(Classes selected) {
        return Arrays.stream(ClassesGroup.values()).filter(o -> o.subclasses.contains(selected)).collect(Collectors.toList()).get(0);
    }

    public static Classes getSelected(Player player) {
        return player.getMetadata("selected-class").stream()
                .map(v -> v.value() instanceof Classes ? (Classes) v.value() : null)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(Classes.CRYOMANCER);
    }

    public static void setSelected(Player player, Classes selectedClass) {
        player.removeMetadata("selected-class", Warlords.getInstance());
        player.setMetadata("selected-class", new FixedMetadataValue(Warlords.getInstance(), selectedClass));
    }

    public static ClassesSkillBoosts getSelectedBoost(Player player) {
        return player.getMetadata("selected-boost").stream()
                .map(v -> v.value() instanceof ClassesSkillBoosts ? (ClassesSkillBoosts) v.value() : null)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(getSelected(player).skillBoosts.get(0));
    }

    public static void setSelectedBoost(Player player, ClassesSkillBoosts selectedBoost) {
        player.removeMetadata("selected-boost", Warlords.getInstance());
        player.setMetadata("selected-boost", new FixedMetadataValue(Warlords.getInstance(), selectedBoost));
    }
}