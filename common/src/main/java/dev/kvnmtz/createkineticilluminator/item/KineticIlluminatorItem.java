package dev.kvnmtz.createkineticilluminator.item;

import com.simibubi.create.foundation.item.CustomArmPoseItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import dev.kvnmtz.createkineticilluminator.item.client.KineticIlluminatorHandler;
import dev.kvnmtz.createkineticilluminator.network.KineticIlluminatorShootPacket;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KineticIlluminatorItem extends Item implements CustomArmPoseItem {

    protected KineticIlluminatorItem(Item.Properties properties) {
        super(properties);
    }

    @ExpectPlatform
    public static KineticIlluminatorItem create(Item.Properties properties) {
        throw new NotImplementedException();
    }

    @Override
    public HumanoidModel.@Nullable ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player,
                                                      InteractionHand hand) {

        if (!player.swinging) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }

        return null;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            var targetPos = KineticIlluminatorHandler.targetPos;
            if (targetPos == null) {
                return super.use(level, player, usedHand);
            }

            new KineticIlluminatorShootPacket(targetPos, KineticIlluminatorHandler.lightLevel, usedHand).sendToServer();
            CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER.dontAnimateItem(usedHand);
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().getCooldowns().isOnCooldown(this)) {
            return InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }
}