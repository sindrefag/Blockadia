package team.hdt.sandboxgame.game_engine.client.rendering;


public class TextureLoader {
/*
    public static final String TEXTURE_PATH = "src/main/resources/assets/sandboxgame/textures/";
    public static final String TEXTURE_TYPE = "matte";
    private static int dirt, grassSide, grassTop, stone, textureSheet;
    private static boolean loaded = false;

    private TextureLoader() {

    }

    public static void bind(Textures texture) {
        int tex = 0;
        switch (texture) {
            case DIRT:
                tex = dirt;
                break;
            case GRASS_SIDE:
                tex = grassSide;
                break;
            case GRASS_TOP:
                tex = grassTop;
                break;
            case STONE:
                tex = stone;
                break;
            case SHEET:
                tex = textureSheet;
                break;
        }
        if (tex == 0)
            System.err.println(String.format("Texture %s not loaded. Will be WHITE", texture));
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex);
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void loadTextures(boolean blur) {
        if (loaded)
            return;
        else {
            int filter;
            if (blur)
                filter = GL_LINEAR;
            else
                filter = GL_NEAREST;
            dirt = glGenTextures();
            grassSide = glGenTextures();
            stone = glGenTextures();
            textureSheet = glGenTextures();
            loadTexture(decodeImage("sheet.png"), textureSheet, filter, filter);
            System.out.println("Successfully loaded textures");
            loaded = true;
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * @param texture    The {@code Texture} object that has the texture information
     * @param textureID  The ID generated by {@code glGenTextures()}
     * @param min_filter {@code GL_NEAREST} for actually showing pixels, {@code GL_LINEAR} for blurring
     * @param mag_filter {@code GL_NEAREST} for actually showing pixels, {@code GL_LINEAR} for blurring
     *
    private static void loadTexture(Texture texture, int textureID, int min_filter, int mag_filter) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min_filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag_filter);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.width, texture.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.buffer);
    }

    private static Texture decodeImage(String tex) {
        InputStream in = null;
        PNGDecoder decoder = null;
        ByteBuffer buffer = null;
        try {
            in = new FileInputStream(TEXTURE_PATH + TEXTURE_TYPE + tex);
            System.out.println(TEXTURE_PATH + TEXTURE_TYPE + tex);
            decoder = new PNGDecoder(in);
            buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
        } catch (IOException e) {
            System.err.println("Could not load texture at: " + TEXTURE_PATH + TEXTURE_TYPE + tex);
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                System.err.println("Could not close the stream");
                e.printStackTrace();
            }
        }
        buffer.flip();
        System.out.println("Texture image: " + decoder.getWidth() + " " + decoder.getHeight());
        return new Texture(buffer, decoder.getWidth(), decoder.getHeight());
    }

    public enum Textures {
        DIRT, GRASS_SIDE, GRASS_TOP, STONE, SHEET
    }
*/
}