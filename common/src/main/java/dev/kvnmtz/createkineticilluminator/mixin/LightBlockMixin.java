package dev.kvnmtz.createkineticilluminator.mixin;

import dev.kvnmtz.createkineticilluminator.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightBlock.class)
public abstract class LightBlockMixin {

    @Inject(
            method = "getShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context,
                          CallbackInfoReturnable<VoxelShape> cir) {

        if (ModItems.KINETIC_ILLUMINATOR != null && context.isHoldingItem(ModItems.KINETIC_ILLUMINATOR.get())) {
            cir.setReturnValue(Shapes.block());
        }
    }

    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    private void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {

        var hasInMainHand = player.getMainHandItem().is(ModItems.KINETIC_ILLUMINATOR.get());
        var hasInOffHand = player.getOffhandItem().is(ModItems.KINETIC_ILLUMINATOR.get());

        if (hasInMainHand || hasInOffHand) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
