package team.hdt.blockadia.engine.core.registries;

import ga.pheonix.utillib.utils.anouncments.Nonnull;
import team.hdt.blockadia.engine.core.util.Identifier;

public class DefaultedHashIdRegistry<T extends RegistryEntry> extends HashIdRegistry<T> {
    private T defaultEntry;

    public DefaultedHashIdRegistry<T> withDefaultEntry(T defaultEntry) {
        this.defaultEntry = defaultEntry;
        return this;
    }

    @Nonnull
    @Override
    public T get(int id) {
        T entry = super.get(id);
        if (entry == null) {
            return this.defaultEntry;
        }
        return entry;
    }

    @Nonnull
    @Override
    public T get(Identifier identifier) {
        T entry = super.get(identifier);
        if (entry == null) {
            return this.defaultEntry;
        }
        return entry;
    }
}
