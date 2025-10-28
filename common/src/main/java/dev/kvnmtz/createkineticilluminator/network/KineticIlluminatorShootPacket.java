package dev.kvnmtz.createkineticilluminator.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import dev.kvnmtz.createkineticilluminator.client.renderer.KineticIlluminatorRenderHandler;
import dev.kvnmtz.createkineticilluminator.registry.ModPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class KineticIlluminatorShootPacket extends BaseS2CMessage {

    public enum ShootType {
        PLACE_BLOCK,
        DESTROY_BLOCK
    }

    public final Vec3 barrelPos;
    public final InteractionHand usedHand;
    public final boolean self;
    public final BlockPos targetPos;
    public final ShootType shootType;

    public KineticIlluminatorShootPacket(Vec3 barrelPos, InteractionHand usedHand, boolean self, BlockPos targetPos,
                                         ShootType shootType) {
        this.barrelPos = barrelPos;
        this.usedHand = usedHand;
        this.self = self;
        this.targetPos = targetPos;
        this.shootType = shootType;
    }

    public KineticIlluminatorShootPacket(FriendlyByteBuf buf) {
        barrelPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        usedHand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        self = buf.readBoolean();
        targetPos = buf.readBlockPos();
        shootType = buf.readBoolean() ? ShootType.PLACE_BLOCK : ShootType.DESTROY_BLOCK;
    }

    @Override
    public MessageType getType() {
        return ModPackets.SHOOT_PACKET;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(barrelPos.x);
        buf.writeDouble(barrelPos.y);
        buf.writeDouble(barrelPos.z);
        buf.writeBoolean(usedHand == InteractionHand.MAIN_HAND);
        buf.writeBoolean(self);
        buf.writeBlockPos(targetPos);
        buf.writeBoolean(shootType == ShootType.PLACE_BLOCK);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            var cameraEntity = Minecraft.getInstance().getCameraEntity();
            if (cameraEntity == null) {
                return;
            }

            if (cameraEntity.position().distanceTo(barrelPos) > 100) {
                return;
            }

            var handler = CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER;

            handler.addBeam(new KineticIlluminatorRenderHandler.PhotonBeam(barrelPos, targetPos.getCenter(),
                    shootType));

            if (self) {
                handler.shoot(usedHand, barrelPos);
                handler.playSound(barrelPos, shootType);
            } else {
                handler.playSound(barrelPos, shootType);
            }
        });
    }
}
