package dev.kvnmtz.createkineticilluminator.item.forge;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import dev.kvnmtz.createkineticilluminator.client.renderer.KineticIlluminatorItemRenderer;
import dev.kvnmtz.createkineticilluminator.item.KineticIlluminatorItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class KineticIlluminatorItemForge extends KineticIlluminatorItem {

    public KineticIlluminatorItemForge(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, KineticIlluminatorItemRenderer.INSTANCE));
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        // TODO: this needs to be done in fabric using EntitySwingListenerItem from porting lib
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        // TODO: seems to be unnecessary
        return slotChanged || !newStack.is(this.asItem());
    }
}
