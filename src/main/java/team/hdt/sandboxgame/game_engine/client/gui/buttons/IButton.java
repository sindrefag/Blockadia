package team.hdt.sandboxgame.game_engine.client.gui.buttons;

import team.hdt.sandboxgame.game_engine.client.gui.GuiTexture;

import java.util.List;

public interface IButton {
    void onClick();

    void whileHover();

    void startHover();

    void stopHover();

    void checkHover();

    void playHoverAnimation(float scaleFactor);

    void playerClickAnimation(float scaleFactor);

    void show_hide(List<GuiTexture> guiTextures);
}
