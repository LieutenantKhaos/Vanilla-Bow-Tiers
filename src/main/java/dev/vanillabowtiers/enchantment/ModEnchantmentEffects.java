package dev.vanillabowtiers.enchantment;

import com.mojang.serialization.MapCodec;
import dev.vanillabowtiers.enchantment.effect.SharpshooterProjectileEffect;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

public final class ModEnchantmentEffects {
    public static final MapCodec<SharpshooterProjectileEffect> SHARPSHOOTER_PROJECTILE =
        register("sharpshooter_projectile", SharpshooterProjectileEffect.CODEC);
    private ModEnchantmentEffects() {}
    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String path, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
            Identifier.fromNamespaceAndPath("vanilla_bow_tiers", path), codec);
    }
    public static void initialize() {}
}
