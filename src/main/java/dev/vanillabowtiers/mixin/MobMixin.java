package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import dev.vanillabowtiers.VanillaBowTiers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "dropCustomDeathLoot", at = @At("TAIL"))
    private void vanillaBowTiers$dropIllusionBow(
            ServerLevel level, DamageSource source, boolean killedByPlayer, CallbackInfo ci) {
        Mob mob = (Mob)(Object)this;
        if (!(mob instanceof AbstractSkeleton skeleton)) return;

        Identifier model = skeleton.getMainHandItem().get(DataComponents.ITEM_MODEL);
        Item drop = null;

        if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "iron_bow").equals(model)) {
            drop = ModItems.IRON_BOW;
        } else if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "golden_bow").equals(model)) {
            drop = ModItems.GOLDEN_BOW;
        } else if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "diamond_bow").equals(model)) {
            drop = ModItems.DIAMOND_BOW;
        } else if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "netherite_bow").equals(model)) {
            drop = ModItems.NETHERITE_BOW;
        }

        if (drop != null && mob.getRandom().nextFloat() < 0.02F) {
            mob.spawnAtLocation(level, new ItemStack(drop));
        }

        // RC.3 persistent ammo drops: only player kills can award VBT arrows.
        // The tier comes from the same one-time ammo assignment stored on the bow,
        // so a Diamond Bow skeleton assigned Golden arrows drops Golden arrows.
        if (!killedByPlayer || !(source.getEntity() instanceof Player player)) return;

        CustomModelData modelData = skeleton.getMainHandItem().get(DataComponents.CUSTOM_MODEL_DATA);
        if (modelData == null || modelData.strings().isEmpty()) return;

        String arrowTier = modelData.strings().getFirst();
        Item arrowDrop;
        float arrowChance;
        int baseMax;

        if ("iron".equals(arrowTier)) {
            arrowDrop = ModItems.IRON_ARROW;
            arrowChance = 0.20F;
            baseMax = 2;
        } else if ("golden".equals(arrowTier)) {
            arrowDrop = ModItems.GOLDEN_ARROW;
            arrowChance = 0.15F;
            baseMax = 2;
        } else if ("diamond".equals(arrowTier)) {
            arrowDrop = ModItems.DIAMOND_ARROW;
            arrowChance = 0.10F;
            baseMax = 1;
        } else if ("netherite".equals(arrowTier)) {
            arrowDrop = ModItems.NETHERITE_ARROW;
            arrowChance = 0.05F;
            baseMax = 1;
        } else {
            // Vanilla-assigned ammo remains entirely under vanilla loot behavior.
            return;
        }

        if (mob.getRandom().nextFloat() >= arrowChance) return;

        int looting = EnchantmentHelper.getEnchantmentLevel(
                level.registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.LOOTING),
                player
        );
        int count = 1 + mob.getRandom().nextInt(baseMax + looting);
        mob.spawnAtLocation(level, new ItemStack(arrowDrop, count));
    }
}
