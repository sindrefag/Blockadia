package net.thegaminghuskymc.sandboxgame.resourcepacks;

import net.thegaminghuskymc.sandboxgame.GameEngine;
import net.thegaminghuskymc.sandboxgame.managers.ResourceManager;

/**
 * a simple wrapper class which make ResourceManager expression shorten
 */
public class R {

    public static String getResPath(String modID, String filepath) {
        return (ResourceManager.instance().getResourcePath(modID, filepath));
    }

    /**
     * get the resource path for a standart resource
     */
    public static String getResPath(String filepath) {
        return (ResourceManager.instance().getResourcePath(GameEngine.instance().getModId(), filepath));
    }

}