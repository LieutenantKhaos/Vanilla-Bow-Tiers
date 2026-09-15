package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import dev.vanillabowtiers.VanillaBowTiers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

        if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "iron_skeleton_bow").equals(model)) {
            drop = ModItems.IRON_BOW;
        } else if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "golden_skeleton_bow").equals(model)) {
            drop = ModItems.GOLDEN_BOW;
        } else if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "diamond_skeleton_bow").equals(model)) {
            drop = ModItems.DIAMOND_BOW;
        } else if (Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "netherite_skeleton_bow").equals(model)) {
            drop = ModItems.NETHERITE_BOW;
        }

        if (drop != null && mob.getRandom().nextFloat() < 0.02F) {
            mob.spawnAtLocation(level, new ItemStack(drop));
        }
    }
}
