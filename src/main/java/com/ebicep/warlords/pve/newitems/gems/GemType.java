package com.ebicep.warlords.pve.newitems.gems;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.NamedEnum;
import org.bukkit.Material;

public enum GemType implements NamedEnum {

    IMPAIRMENT("Gemstone of Impairment", NewItemAttribute.DAMAGE, .25f, Material.REDSTONE),
    ALLEVIATION("Gemstone of Alleviation", NewItemAttribute.HEALING, .25f, Material.GLOWSTONE_DUST),
    SURGES("Gemstone of Surges", NewItemAttribute.MAX_ENERGY, 5, Material.GLOW_INK_SAC),
    NOURISHMENT("Gemstone of Nourishment", NewItemAttribute.HEALTH, 10, Material.GHAST_TEAR),
    SPEED("Gemstone of Speed", NewItemAttribute.ATTACK_SPEED, 2, Material.SUGAR),

    ;

    public static final GemType[] VALUES = values();

    private final String name;
    private final NewItemAttribute attribute;
    private final float valuePerTier;
    private final Material material;

    GemType(String name, NewItemAttribute attribute, float valuePerTier, Material material) {
        this.name = name;
        this.attribute = attribute;
        this.valuePerTier = valuePerTier;
        this.material = material;
    }

    @Override
    public String getName() {
        return name;
    }

    public NewItemAttribute getAttribute() {
        return attribute;
    }

    public float getValuePerTier() {
        return valuePerTier;
    }

    public Material getMaterial() {
        return material;
    }

}
