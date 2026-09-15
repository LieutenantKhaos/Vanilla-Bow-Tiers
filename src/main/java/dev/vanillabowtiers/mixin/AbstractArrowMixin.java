package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import dev.vanillabowtiers.access.EnderiteArrowAccess;
import dev.vanillabowtiers.access.SharpshooterArrowAccess;
import dev.vanillabowtiers.access.ProjectileTierAccess;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements SharpshooterArrowAccess, EnderiteArrowAccess, ProjectileTierAccess {
    @Unique
    private static final EntityDataAccessor<Byte> VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL =
            SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    @Unique
    private static final EntityDataAccessor<Byte> VANILLA_BOW_TIERS$PROJECTILE_TIER =
            SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);


    @Unique
    private boolean vanillaBowTiers$enderiteArrow = false;

    @Unique
    private boolean vanillaBowTiers$checkedWeakEnderiteShot = false;

    @Unique
    private int vanillaBowTiers$weakEnderiteShotTicks = -1;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void vanillaBowTiers$defineSharpshooterData(
            SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL, (byte) 0);
        builder.define(VANILLA_BOW_TIERS$PROJECTILE_TIER, (byte) 0);
    }

    @Override
    public void vanillaBowTiers$setSharpshooterLevel(int level) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;
        arrow.getEntityData().set(
                VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL,
                (byte)Math.max(0, Math.min(4, level))
        );
    }

    @Override
    public int vanillaBowTiers$getSharpshooterLevel() {
        AbstractArrow arrow = (AbstractArrow)(Object)this;
        return arrow.getEntityData().get(VANILLA_BOW_TIERS$SHARPSHOOTER_LEVEL);
    }


    @Override
    public void vanillaBowTiers$setProjectileTier(int tier) {
        ((AbstractArrow)(Object)this).getEntityData().set(
                VANILLA_BOW_TIERS$PROJECTILE_TIER, (byte)Math.max(0, Math.min(5, tier)));
    }

    @Override
    public int vanillaBowTiers$getProjectileTier() {
        return ((AbstractArrow)(Object)this).getEntityData().get(VANILLA_BOW_TIERS$PROJECTILE_TIER);
    }

    @Override
    public void vanillaBowTiers$markEnderiteArrow() {
        vanillaBowTiers$enderiteArrow = true;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void vanillaBowTiers$recoverWeakEnderiteArrow(CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;

        if (!vanillaBowTiers$enderiteArrow || arrow.level().isClientSide()) {
            return;
        }

        if (!vanillaBowTiers$checkedWeakEnderiteShot) {
            vanillaBowTiers$checkedWeakEnderiteShot = true;

            // Only very weak releases get the recovery behavior. Normal Enderite
            // Arrow shots remain ordinary projectiles.
            if (arrow.getDeltaMovement().lengthSqr() < 0.25D) {
                vanillaBowTiers$weakEnderiteShotTicks = 60;
            }
        }

        if (vanillaBowTiers$weakEnderiteShotTicks > 0) {
            vanillaBowTiers$weakEnderiteShotTicks--;
        }

        if (vanillaBowTiers$weakEnderiteShotTicks == 0) {
            ItemEntity droppedArrow = new ItemEntity(
                    arrow.level(),
                    arrow.getX(),
                    arrow.getY(),
                    arrow.getZ(),
                    new ItemStack(ModItems.ENDERITE_ARROW)
            );
            droppedArrow.setDeltaMovement(arrow.getDeltaMovement().scale(0.25D));
            arrow.level().addFreshEntity(droppedArrow);
            arrow.discard();
            vanillaBowTiers$weakEnderiteShotTicks = -1;
        }
    }

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), cancellable = true)
    private void vanillaBowTiers$sharpshooterGravity(CallbackInfoReturnable<Double> cir) {
        int level = vanillaBowTiers$getSharpshooterLevel();
        double factor = switch (level) {
            case 1 -> 0.75D;
            case 2 -> 0.50D;
            case 3 -> 0.25D;
            case 4 -> 0.10D;
            default -> 1.0D;
        };

        if (factor != 1.0D) {
            cir.setReturnValue(cir.getReturnValue() * factor);
        }
    }
}
