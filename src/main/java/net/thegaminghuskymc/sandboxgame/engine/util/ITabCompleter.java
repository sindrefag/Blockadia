package net.thegaminghuskymc.sandboxgame.engine.util;

public interface ITabCompleter {
    /**
     * Sets the list of tab completions, as long as they were previously requested.
     */
    void setCompletions(String... newCompletions);
}