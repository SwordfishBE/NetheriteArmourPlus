package net.netheritearmourplus.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.netheritearmourplus.effect.ArmorEffectService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets Snow Walker act like safe powder-snow boots.
 */
@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void netheritearmourplus$allowSnowWalker(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ArmorEffectService.canWalkOnPowderSnow(entity)) {
            cir.setReturnValue(true);
        }
    }
}
