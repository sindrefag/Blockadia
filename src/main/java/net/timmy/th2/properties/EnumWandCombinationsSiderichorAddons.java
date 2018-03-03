package net.timmy.th2.properties;

import net.thegaminghuskymc.sandboxgame.util.IStringSerializable;

public enum EnumWandCombinationsSiderichorAddons implements IStringSerializable {

    SIDERICHOR_AND_BIG_OAK("siderichor_big_oak", 0),
    SIDERICHOR_AND_ACACIA("siderichor_acacia", 1),
    SIDERICHOR_AND_JUNGLE("siderichor_jungle", 2),
    SIDERICHOR_AND_BIRCH("siderichor_birch", 3),
    SIDERICHOR_AND_THORNWYRD("siderichor_thornwyrd", 4),
    SIDERICHOR_AND_GRAND_OAK("siderichor_grand_oak", 5),
    SIDERICHOR_AND_SPRUCE("siderichor_spruce", 6);

    private static final EnumWandCombinationsSiderichorAddons[] METADATA_LOOKUP = new EnumWandCombinationsSiderichorAddons[values().length];

    static {
        for (EnumWandCombinationsSiderichorAddons type : values()) {
            METADATA_LOOKUP[type.getID()] = type;
        }
    }

    protected String name;
    protected int ID;

    EnumWandCombinationsSiderichorAddons(String name, int ID) {
        this.name = name;
        this.ID = ID;
    }

    public static EnumWandCombinationsSiderichorAddons byMetadata(int metadata) {

        if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
            metadata = 0;
        }
        return METADATA_LOOKUP[metadata];
    }

    public static String[] toStringArray() {
        String[] array = new String[values().length];

        for (int i = 0; i < array.length; i++) {
            array[i] = values()[i].name;
        }

        return array;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.ID;
    }

}