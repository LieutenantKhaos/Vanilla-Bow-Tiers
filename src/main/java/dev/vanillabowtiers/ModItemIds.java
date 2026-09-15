package dev.vanillabowtiers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItemIds {
    private ModItemIds() {}

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, name)
        );
    }

    public static final ResourceKey<Item> IRON_BOW = create("iron_bow");
    public static final ResourceKey<Item> GOLDEN_BOW = create("golden_bow");
    public static final ResourceKey<Item> DIAMOND_BOW = create("diamond_bow");
    public static final ResourceKey<Item> NETHERITE_BOW = create("netherite_bow");

    public static final ResourceKey<Item> IRON_ARROW = create("iron_arrow");
    public static final ResourceKey<Item> GOLDEN_ARROW = create("golden_arrow");
    public static final ResourceKey<Item> DIAMOND_ARROW = create("diamond_arrow");
    public static final ResourceKey<Item> NETHERITE_ARROW = create("netherite_arrow");
    public static final ResourceKey<Item> ENDERITE_ARROW = create("enderite_arrow");
}
