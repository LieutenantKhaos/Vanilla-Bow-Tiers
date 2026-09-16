package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import dev.vanillabowtiers.VanillaBowTiers;
import dev.vanillabowtiers.TieredArrowItem;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
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
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "iron_bow");
    private static final Identifier GOLD_MODEL =
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "golden_bow");
    private static final Identifier DIAMOND_MODEL =
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "diamond_bow");
    private static final Identifier NETHERITE_MODEL =
            Identifier.fromNamespaceAndPath(VanillaBowTiers.MOD_ID, "netherite_bow");

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

        ItemStack bow;
        if (model != null) {
            // This is deliberately a real vanilla bow. Skeleton AI and posing
            // therefore remain 100% vanilla; ITEM_MODEL changes appearance only.
            bow = new ItemStack(Items.BOW);
            bow.set(DataComponents.ITEM_MODEL, model);
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);

            // Never drop the disguised vanilla bow. MobMixin supplies the real
            // VBT tier reward with its own controlled chance.
            skeleton.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            skeleton.reassessWeaponGoal();
        } else if (skeleton instanceof Skeleton && skeleton.getMainHandItem().is(Items.BOW)) {
            // Normal bow skeletons also get one persistent ammo roll.
            bow = skeleton.getMainHandItem();
        } else {
            return;
        }

        String arrowTier = vanillaBowTiers$rollArrowTier(skeleton, model);
        bow.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(List.of(), List.of(), List.of(arrowTier), List.of())
        );
    }

    private static String vanillaBowTiers$rollArrowTier(AbstractSkeleton skeleton, @Nullable Identifier model) {
        float roll = skeleton.getRandom().nextFloat();

        if (IRON_MODEL.equals(model)) return roll < 0.40F ? "vanilla" : "iron";
        if (GOLD_MODEL.equals(model)) {
            if (roll < 0.20F) return "vanilla";
            if (roll < 0.60F) return "iron";
            return "golden";
        }
        if (DIAMOND_MODEL.equals(model)) {
            if (roll < 0.40F) return "iron";
            if (roll < 0.70F) return "golden";
            return "diamond";
        }
        if (NETHERITE_MODEL.equals(model)) {
            if (roll < 0.35F) return "iron";
            if (roll < 0.65F) return "golden";
            if (roll < 0.90F) return "diamond";
            return "netherite";
        }
        return roll < 0.80F ? "vanilla" : "iron";
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
        ItemStack bow = skeleton.getMainHandItem();
        Identifier model = bow.get(DataComponents.ITEM_MODEL);
        CustomModelData modelData = bow.get(DataComponents.CUSTOM_MODEL_DATA);
        String arrowTier = modelData == null || modelData.strings().isEmpty()
                ? null : modelData.strings().getFirst();

        double bowMultiplier;
        if (IRON_MODEL.equals(model)) bowMultiplier = 1.10D;
        else if (GOLD_MODEL.equals(model)) bowMultiplier = 1.00D;
        else if (DIAMOND_MODEL.equals(model)) bowMultiplier = 1.20D;
        else if (NETHERITE_MODEL.equals(model)) bowMultiplier = 1.35D;
        else if (bow.is(Items.BOW) && arrowTier != null) bowMultiplier = 1.00D;
        else return;

        ArrowItem arrowItem;
        double arrowMultiplier;
        if ("iron".equals(arrowTier)) {
            arrowItem = (TieredArrowItem) ModItems.IRON_ARROW;
            arrowMultiplier = ((TieredArrowItem) arrowItem).damageMultiplier();
        } else if ("golden".equals(arrowTier)) {
            arrowItem = (TieredArrowItem) ModItems.GOLDEN_ARROW;
            arrowMultiplier = ((TieredArrowItem) arrowItem).damageMultiplier();
        } else if ("diamond".equals(arrowTier)) {
            arrowItem = (TieredArrowItem) ModItems.DIAMOND_ARROW;
            arrowMultiplier = ((TieredArrowItem) arrowItem).damageMultiplier();
        } else if ("netherite".equals(arrowTier)) {
            arrowItem = (TieredArrowItem) ModItems.NETHERITE_ARROW;
            arrowMultiplier = ((TieredArrowItem) arrowItem).damageMultiplier();
        } else {
            arrowItem = (ArrowItem) Items.ARROW;
            arrowMultiplier = 1.00D;
        }

        // Preserve RC.2's proven creation path: the selected ArrowItem creates
        // the projectile, so TieredArrowItem remains authoritative for tier,
        // pickup identity, texture state and arrow-tier damage.
        ItemStack ammo = new ItemStack(arrowItem);
        AbstractArrow arrow = arrowItem.createArrow(
                skeleton.level(), ammo, skeleton, firingWeapon);
        arrow.setBaseDamageFromMob((float)(2.0D * arrowMultiplier * bowMultiplier));
        cir.setReturnValue(arrow);
    }
}
