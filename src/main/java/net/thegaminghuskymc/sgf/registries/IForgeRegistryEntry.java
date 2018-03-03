package net.thegaminghuskymc.sgf.registries;

import com.google.common.reflect.TypeToken;
import net.thegaminghuskymc.sgf.fml.common.Loader;
import net.thegaminghuskymc.sgf.fml.common.ModContainer;
import net.thegaminghuskymc.sandboxgame.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IForgeRegistryEntry<V>
{
    /**
     * Sets a unique name for this Item. This should be used for uniquely identify the instance of the Item.
     * This is the valid replacement for the atrocious 'getUnlocalizedName().substring(6)' stuff that everyone does.
     * Unlocalized names have NOTHING to do with unique identifiers. As demonstrated by vanilla blocks and items.
     *
     * The supplied name will be prefixed with the currently active mod's modId.
     * If the supplied name already has a prefix that is different, it will be used and a warning will be logged.
     *
     * If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.
     *
     * Returns 'this' to allow for chaining.
     *
     * @param name Unique registry name
     * @return This instance
     */
    V setRegistryName(ResourceLocation name);

    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * Otherwise it will return the name set in setRegistryName().
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    @Nullable
    ResourceLocation getRegistryName();

    Class<V> getRegistryType();

    // Default implementation, modders who make extra items SHOULD extend this instead of Object.
    // So, all fields in interfaces are forced static, so even with Java8 people must still extend this.
    @SuppressWarnings({ "serial", "unchecked" })
    public static class Impl<T  extends IForgeRegistryEntry<T>> implements IForgeRegistryEntry<T>
    {
        private TypeToken<T> token = new TypeToken<T>(getClass()){};
        public final IRegistryDelegate<T> delegate = new RegistryDelegate<T>((T)this, (Class<T>)token.getRawType());
        private ResourceLocation registryName = null;

        public final T setRegistryName(String name)
        {
            if (getRegistryName() != null)
                throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

            int index = name.lastIndexOf(':');
            String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase();
            name = index == -1 ? name : name.substring(index + 1);
            ModContainer mc = Loader.instance().activeModContainer();
            String prefix = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
            if (!oldPrefix.equals(prefix) && oldPrefix.length() > 0)
            {
                FMLLog.log.info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
                prefix = oldPrefix;
            }
            this.registryName = new ResourceLocation(prefix, name);
            return (T)this;
        }

        //Helper functions
        @Override
        public final T setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }
        public final T setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }
        @Override
        @Nullable
        public final ResourceLocation getRegistryName()
        {
            if (delegate.name() != null) return delegate.name();
            return registryName != null ? registryName : null;
        }

        @Override
        public final Class<T> getRegistryType() { return (Class<T>) token.getRawType(); };
    }
}