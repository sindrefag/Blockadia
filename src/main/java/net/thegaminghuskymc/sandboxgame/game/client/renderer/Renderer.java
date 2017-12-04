package net.thegaminghuskymc.sandboxgame.game.client.renderer;

import net.thegaminghuskymc.sandboxgame.engine.Logger;
import net.thegaminghuskymc.sandboxgame.engine.Taskable;
import net.thegaminghuskymc.sandboxgame.engine.Timer;
import net.thegaminghuskymc.sandboxgame.game.client.opengl.window.GLFWWindow;

public abstract class Renderer implements Taskable {

    private final MainRenderer mainRenderer;

    public Renderer(MainRenderer mainRenderer) {
        Logger.get().log(Logger.Level.FINE, "instancing new " + this.getClass().getSimpleName());
        this.mainRenderer = mainRenderer;
    }

    public final MainRenderer getMainRenderer() {
        return (this.mainRenderer);
    }

    /**
     * call on initialization : load your shaders / and buffer heres
     */
    public abstract void initialize();

    /**
     * call on deitniailiation : unload shaders / buffers
     */
    public abstract void deinitialize();

    /**
     * a callback when the window is resized
     */
    public void onWindowResize(GLFWWindow window) {
    }

    public void render() {
    }

    public final Timer getTimer() {
        return (this.getMainRenderer().getEngine().getTimer());
    }
}
