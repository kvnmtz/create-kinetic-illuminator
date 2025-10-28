package dev.kvnmtz.createkineticilluminator.item.fabric;

import dev.kvnmtz.createkineticilluminator.item.KineticIlluminatorItem;
import io.github.fabricators_of_create.porting_lib.item.EntitySwingListenerItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class KineticIlluminatorItemFabric extends KineticIlluminatorItem implements EntitySwingListenerItem {

    public KineticIlluminatorItemFabric(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntitySwing(ItemStack itemStack, LivingEntity livingEntity) {
        return true;
    }
}
