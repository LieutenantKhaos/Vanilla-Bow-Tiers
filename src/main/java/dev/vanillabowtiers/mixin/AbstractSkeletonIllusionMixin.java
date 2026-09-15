package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import dev.vanillabowtiers.VanillaBowTiers;
import dev.vanillabowtiers.TieredArrowItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonIllusionMixin {
    private static final Identifier IRON_MODEL =
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "iron_skeleton_bow");
    private static final Identifier GOLD_MODEL =
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "golden_skeleton_bow");
    private static final Identifier DIAMOND_MODEL =
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "diamond_skeleton_bow");
    private static final Identifier NETHERITE_MODEL =
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "netherite_skeleton_bow");

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void vanillaBowTiers$assignIllusionTier(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData spawnData,
            CallbackInfoReturnable<SpawnGroupData> cir
    ) {
        AbstractSkeleton skeleton = (AbstractSkeleton)(Object)this;
        float roll = skeleton.getRandom().nextFloat();
        Identifier model = null;

        if (skeleton instanceof WitherSkeleton) {
            if (roll < 0.01F) model = NETHERITE_MODEL;
            else if (roll < 0.06F) model = DIAMOND_MODEL;
            else if (roll < 0.16F) model = GOLD_MODEL;
        } else if (skeleton instanceof Skeleton) {
            if (roll < 0.03F) model = DIAMOND_MODEL;
            else if (roll < 0.10F) model = GOLD_MODEL;
            else if (roll < 0.25F) model = IRON_MODEL;
        }

        if (model != null) {
            // This is deliberately a real vanilla bow. Skeleton AI and posing
            // therefore remain 100% vanilla; ITEM_MODEL changes appearance only.
            ItemStack bow = new ItemStack(Items.BOW);
            bow.set(DataComponents.ITEM_MODEL, model);

            // Bray compatibility uses a skeleton-specific item model that
            // hardcodes the matching tiered nocked arrow while drawing.
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);

            // Never drop the disguised vanilla bow. MobMixin supplies the real
            // VBT tier reward with its own controlled chance.
            skeleton.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            skeleton.reassessWeaponGoal();
        }
    }

    @Inject(method = "canUseNonMeleeWeapon", at = @At("HEAD"), cancellable = true)
    private void vanillaBowTiers$acceptDisguisedVanillaBow(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // MC 26.2 reassessWeaponGoal gates the ranged goal through this
        // ItemStack predicate. The illusion weapon is a real minecraft:bow;
        // only accept stacks carrying one of our tier model markers.
        if (stack.is(Items.BOW)) {
            Identifier model = stack.get(DataComponents.ITEM_MODEL);
            if (IRON_MODEL.equals(model)
                    || GOLD_MODEL.equals(model)
                    || DIAMOND_MODEL.equals(model)
                    || NETHERITE_MODEL.equals(model)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getArrow", at = @At("HEAD"), cancellable = true)
    private void vanillaBowTiers$createRealTieredArrow(
            ItemStack projectile,
            float power,
            @Nullable ItemStack firingWeapon,
            CallbackInfoReturnable<AbstractArrow> cir
    ) {
        AbstractSkeleton skeleton = (AbstractSkeleton)(Object)this;
        Identifier model = skeleton.getMainHandItem().get(DataComponents.ITEM_MODEL);

        TieredArrowItem arrowItem;
        double bowMultiplier;
        if (IRON_MODEL.equals(model)) {
            arrowItem = (TieredArrowItem) ModItems.IRON_ARROW;
            bowMultiplier = 1.10D;
        } else if (GOLD_MODEL.equals(model)) {
            arrowItem = (TieredArrowItem) ModItems.GOLDEN_ARROW;
            bowMultiplier = 1.00D;
        } else if (DIAMOND_MODEL.equals(model)) {
            arrowItem = (TieredArrowItem) ModItems.DIAMOND_ARROW;
            bowMultiplier = 1.20D;
        } else if (NETHERITE_MODEL.equals(model)) {
            arrowItem = (TieredArrowItem) ModItems.NETHERITE_ARROW;
            bowMultiplier = 1.35D;
        } else {
            return;
        }

        // Do not ask vanilla's skeleton ammo path to reinterpret a substituted
        // ItemStack. Construct the projectile through the actual VBT ArrowItem,
        // exactly like a player firing that tier. This lets TieredArrowItem set
        // projectile tier, pickup item and tier base damage at the source.
        ItemStack tieredAmmo = new ItemStack(arrowItem);
        AbstractArrow arrow = arrowItem.createArrow(
                skeleton.level(), tieredAmmo, skeleton, firingWeapon);

        // TieredArrowItem already applies the arrow multiplier. Apply the mob's
        // illusion-bow damage multiplier on top without duplicating tier state.
        arrow.setBaseDamageFromMob((float)(2.0D * arrowItem.damageMultiplier() * bowMultiplier));
        cir.setReturnValue(arrow);
    }

}
