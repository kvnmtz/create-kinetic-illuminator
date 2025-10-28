package dev.kvnmtz.createkineticilluminator.item.forge;

import dev.kvnmtz.createkineticilluminator.item.KineticIlluminatorItem;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class KineticIlluminatorItemImpl {

    public static KineticIlluminatorItem create(Item.Properties properties) {
        return new KineticIlluminatorItemForge(properties);
    }
}
