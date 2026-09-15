package dev.vanillabowtiers;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ModItems {
    private ModItems() {}

    private static Item register(
            ResourceKey<Item> key,
            Function<Item.Properties, Item> factory,
            Item.Properties properties
    ) {
        Item item = factory.apply(properties.setId(key));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }

    // Bow velocity multiplier is intentionally conservative: velocity also
    // influences projectile damage, so small increases are meaningful.
    public static final Item IRON_BOW = register(
        ModItemIds.IRON_BOW,
        p -> new TieredBowItem(p, 1.10f, 1.05f, 1.05f),
        new Item.Properties().durability(500).rarity(Rarity.COMMON)
    );

    // Gold: fragile, but the strongest "quick/fast" feeling tier.
    public static final Item GOLDEN_BOW = register(
        ModItemIds.GOLDEN_BOW,
        p -> new TieredBowItem(p, 1.00f, 1.12f, 1.30f),
        new Item.Properties().durability(192).rarity(Rarity.UNCOMMON)
    );

    public static final Item DIAMOND_BOW = register(
        ModItemIds.DIAMOND_BOW,
        p -> new TieredBowItem(p, 1.20f, 1.12f, 1.10f),
        new Item.Properties().durability(1024).rarity(Rarity.RARE)
    );

    public static final Item NETHERITE_BOW = register(
        ModItemIds.NETHERITE_BOW,
        p -> new TieredBowItem(p, 1.35f, 1.18f, 1.15f),
        new Item.Properties().durability(1536).fireResistant().rarity(Rarity.RARE)
    );

    public static final Item IRON_ARROW = register(
        ModItemIds.IRON_ARROW,
        p -> new TieredArrowItem(p, 1.50),
        new Item.Properties().rarity(Rarity.COMMON)
    );

    public static final Item GOLDEN_ARROW = register(
        ModItemIds.GOLDEN_ARROW,
        p -> new TieredArrowItem(p, 1.10),
        new Item.Properties().rarity(Rarity.UNCOMMON)
    );

    public static final Item DIAMOND_ARROW = register(
        ModItemIds.DIAMOND_ARROW,
        p -> new TieredArrowItem(p, 1.50),
        new Item.Properties().rarity(Rarity.RARE)
    );

    public static final Item NETHERITE_ARROW = register(
        ModItemIds.NETHERITE_ARROW,
        p -> new TieredArrowItem(p, 1.75),
        new Item.Properties().fireResistant().rarity(Rarity.RARE)
    );

    // Enderite integration tier: successor to Netherite when Enderite Mod is present.
    public static final Item ENDERITE_ARROW = register(
        ModItemIds.ENDERITE_ARROW,
        p -> new TieredArrowItem(p, 2.50),
        new Item.Properties().fireResistant().rarity(Rarity.EPIC)
    );

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(IRON_BOW);
            entries.accept(GOLDEN_BOW);
            entries.accept(DIAMOND_BOW);
            entries.accept(NETHERITE_BOW);
            entries.accept(IRON_ARROW);
            entries.accept(GOLDEN_ARROW);
            entries.accept(DIAMOND_ARROW);
            entries.accept(NETHERITE_ARROW);
            entries.accept(ENDERITE_ARROW);
        });
    }
}
