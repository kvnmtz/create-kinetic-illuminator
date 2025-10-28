package dev.kvnmtz.createkineticilluminator.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.Iris;

@Environment(EnvType.CLIENT)
public abstract class ShaderUtils {

    private static Boolean irisLoaded = null;

    private static boolean isIrisLoaded() {
        if (irisLoaded == null) {
            try {
                Class.forName("net.irisshaders.iris.Iris");
                irisLoaded = true;
            } catch (ClassNotFoundException e) {
                irisLoaded = false;
            }
        }

        return irisLoaded;
    }

    public static boolean areShadersEnabled() {
        if (!isIrisLoaded()) {
            return false;
        }

        return IrisUtils.areShadersEnabled();
    }

    // this class must only be loaded if the iris class exists
    private static class IrisUtils {

        public static boolean areShadersEnabled() {
            return Iris.getIrisConfig().areShadersEnabled();
        }
    }
}
