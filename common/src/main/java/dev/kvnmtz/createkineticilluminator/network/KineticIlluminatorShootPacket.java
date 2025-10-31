package dev.kvnmtz.createkineticilluminator.network;

import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.kvnmtz.createkineticilluminator.registry.ModItems;
import dev.kvnmtz.createkineticilluminator.registry.ModPackets;
import dev.kvnmtz.createkineticilluminator.util.SoundUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class KineticIlluminatorShootPacket extends BaseC2SMessage {

    private final BlockPos targetPos;
    private final int lightLevel;
    private final InteractionHand usedHand;

    public KineticIlluminatorShootPacket(BlockPos targetPos, int lightLevel, InteractionHand usedHand) {
        this.targetPos = targetPos;
        this.lightLevel = lightLevel;
        this.usedHand = usedHand;
    }

    public KineticIlluminatorShootPacket(FriendlyByteBuf buf) {
        targetPos = buf.readBlockPos();
        lightLevel = buf.readByte();
        usedHand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    @Override
    public MessageType getType() {
        return ModPackets.SHOOT_PACKET;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(targetPos);
        buf.writeByte(lightLevel);
        buf.writeBoolean(usedHand == InteractionHand.MAIN_HAND);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            var player = (ServerPlayer) context.getPlayer();

            if (player.getEyePosition().distanceTo(targetPos.getCenter()) >= 10) {
                return;
            }

            if (lightLevel < 1 || lightLevel > 15) {
                return;
            }

            var serverLevel = player.serverLevel();
            var blockState = serverLevel.getBlockState(targetPos);

            var destroyLightBlock = blockState.is(Blocks.LIGHT);
            if (destroyLightBlock) {
                destroyLightBlock(serverLevel, blockState, targetPos);
            } else {
                if (!placeLightBlock(lightLevel, serverLevel, blockState, targetPos, player)) {
                    return;
                }
            }

            var mainHand = usedHand == InteractionHand.MAIN_HAND;
            var barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(player, mainHand, new Vec3(.35f, -0.1f, 1));
            var shootType = destroyLightBlock ? KineticIlluminatorBeamPacket.ShootType.DESTROY_BLOCK :
                    KineticIlluminatorBeamPacket.ShootType.PLACE_BLOCK;

            player.getCooldowns().addCooldown(ModItems.KINETIC_ILLUMINATOR.get(), 10);

            new KineticIlluminatorBeamPacket(barrelPos, usedHand, false, targetPos, shootType).sendTo(ModPackets.getTrackingPlayers(player));
            new KineticIlluminatorBeamPacket(barrelPos, usedHand, true, targetPos, shootType).sendTo(player);
        });
    }

    private static final int FLAG_BLOCK_UPDATE = 1;
    private static final int FLAG_CLIENT_SYNC = 2;

    private static int getRequiredGlowstoneDust(int lightLevel) {
        if (lightLevel <= 3) return 1;
        if (lightLevel <= 7) return 2;
        if (lightLevel <= 11) return 3;
        return 4;
    }

    private static void destroyLightBlock(ServerLevel serverLevel, BlockState blockState, BlockPos targetPos) {
        if (blockState.getValue(LightBlock.WATERLOGGED)) {
            serverLevel.setBlock(targetPos, Blocks.WATER.defaultBlockState(),
                    FLAG_BLOCK_UPDATE | FLAG_CLIENT_SYNC);
        } else {
            serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), FLAG_CLIENT_SYNC);
        }
    }

    private static boolean placeLightBlock(int lightLevel, ServerLevel serverLevel, BlockState blockState,
                                           BlockPos targetPos, ServerPlayer player) {

        var isWater = blockState.is(Blocks.WATER);
        if (!blockState.isAir() && !isWater) {
            return false;
        }

        var isCreative = player.getAbilities().instabuild;
        if (!isCreative) {
            if (!couldGlowstoneBeConsumed(lightLevel, player)) {
                return false;
            }
        }

        var lightBlockState = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, lightLevel);

        if (isWater) {
            lightBlockState = lightBlockState.setValue(LightBlock.WATERLOGGED, true);
        }

        serverLevel.setBlock(targetPos, lightBlockState, FLAG_CLIENT_SYNC);

        return true;
    }

    private static boolean couldGlowstoneBeConsumed(int lightLevel, ServerPlayer player) {
        var requiredDust = getRequiredGlowstoneDust(lightLevel);
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
}
