package dev.vanillabowtiers.enchantment.effect;

import com.mojang.serialization.MapCodec;
import dev.vanillabowtiers.access.SharpshooterArrowAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record SharpshooterProjectileEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<SharpshooterProjectileEffect> CODEC = MapCodec.unit(SharpshooterProjectileEffect::new);

    @Override
    public void apply(ServerLevel serverLevel, int level, EnchantedItemInUse context, Entity target, Vec3 pos) {
        if (target instanceof SharpshooterArrowAccess arrow) {
            arrow.vanillaBowTiers$setSharpshooterLevel(level);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
