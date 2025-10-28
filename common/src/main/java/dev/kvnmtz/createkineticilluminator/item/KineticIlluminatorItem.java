package dev.kvnmtz.createkineticilluminator.item;

import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import dev.kvnmtz.createkineticilluminator.item.client.KineticIlluminatorHandler;
import dev.kvnmtz.createkineticilluminator.network.KineticIlluminatorShootPacket;
import dev.kvnmtz.createkineticilluminator.registry.ModPackets;
import dev.kvnmtz.createkineticilluminator.util.SoundUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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

    private static final int FLAG_BLOCK_UPDATE = 1;
    private static final int FLAG_CLIENT_SYNC = 2;

    /**
     * Calculate required glowstone dust based on light level
     * <=3: 1 dust, 4-7: 2 dust, 8-11: 3 dust, >=12: 4 dust
     */
    private static int getRequiredGlowstoneDust(int lightLevel) {
        if (lightLevel <= 3) return 1;
        if (lightLevel <= 7) return 2;
        if (lightLevel <= 11) return 3;
        return 4; // lightLevel >= 12
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var targetPos = KineticIlluminatorHandler.targetPos;
        if (targetPos == null) {
            return super.use(level, player, usedHand);
        }

        player.startUsingItem(usedHand);

        if (level instanceof ServerLevel serverLevel) {
            var blockState = serverLevel.getBlockState(targetPos);

            var destroyLightBlock = blockState.is(Blocks.LIGHT);
            if (destroyLightBlock) {
                destroyLightBlock(serverLevel, blockState, targetPos);
            } else {
                if (!placeLightBlock(serverLevel, blockState, targetPos, (ServerPlayer) player)) {
                    return super.use(level, player, usedHand);
                }
            }

            var mainHand = usedHand == InteractionHand.MAIN_HAND;
            var barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(player, mainHand, new Vec3(.35f, -0.1f, 1));
            var shootType = destroyLightBlock ? KineticIlluminatorShootPacket.ShootType.DESTROY_BLOCK :
                    KineticIlluminatorShootPacket.ShootType.PLACE_BLOCK;

            player.getCooldowns().addCooldown(this, 10);

            new KineticIlluminatorShootPacket(barrelPos, usedHand, false, targetPos, shootType).sendTo(ModPackets.getTrackingPlayers((ServerPlayer) player));
            new KineticIlluminatorShootPacket(barrelPos, usedHand, true, targetPos, shootType).sendTo((ServerPlayer) player);
        } else {
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

    private static void destroyLightBlock(ServerLevel serverLevel, BlockState blockState, BlockPos targetPos) {
        if (blockState.getValue(LightBlock.WATERLOGGED)) {
            serverLevel.setBlock(targetPos, Blocks.WATER.defaultBlockState(),
                    FLAG_BLOCK_UPDATE | FLAG_CLIENT_SYNC);
        } else {
            serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), FLAG_CLIENT_SYNC);
        }
    }

    private static boolean placeLightBlock(ServerLevel serverLevel, BlockState blockState, BlockPos targetPos,
                                           ServerPlayer player) {
        var isWater = blockState.is(Blocks.WATER);
        if (!blockState.isAir() && !isWater) {
            return false;
        }

        var isCreative = player.getAbilities().instabuild;
        if (!isCreative) {
            if (!couldGlowstoneBeConsumed(player)) {
                return false;
            }
        }

        var lightBlockState = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL,
                KineticIlluminatorHandler.lightLevel);

        if (isWater) {
            lightBlockState = lightBlockState.setValue(LightBlock.WATERLOGGED, true);
        }

        serverLevel.setBlock(targetPos, lightBlockState, FLAG_CLIENT_SYNC);

        return true;
    }

    private static boolean couldGlowstoneBeConsumed(ServerPlayer player) {
        var requiredDust = getRequiredGlowstoneDust(KineticIlluminatorHandler.lightLevel);
        var inventory = player.getInventory();

        var availableDust = 0;
        for (var stack : inventory.items) {
            if (stack.is(Items.GLOWSTONE_DUST)) {
                availableDust += stack.getCount();
            }
        }

        if (availableDust < requiredDust) {
            SoundUtils.playSoundForPlayer(player, SoundEvents.NOTE_BLOCK_BASS.value(), 1.0f, 0.5f);
            player.displayClientMessage(Component.literal("§cNot enough Glowstone Dust"), true);
            return false;
        }

        var dustToRemove = requiredDust;
        for (var stack : inventory.items) {
            if (stack.is(Items.GLOWSTONE_DUST)) {
                var removeFromStack = Math.min(dustToRemove, stack.getCount());
                stack.shrink(removeFromStack);
                dustToRemove -= removeFromStack;
            }

            if (dustToRemove == 0) {
                break;
            }
        }

        return true;
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