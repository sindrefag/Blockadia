package team.hdt.blockadia.test;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import team.hdt.blockadia.game_engine.common.world.World;
import team.hdt.blockadia.game_engine.common.world.biomes.Biomes;
import team.hdt.blockadia.game_engine.common.world.block.BlockTypes;
import team.hdt.blockadia.game_engine.common.world.block.BlockUtils;
import team.hdt.blockadia.game_engine.common.world.gen.factory.WorldFactory;

import java.io.File;
import java.io.FileWriter;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private World world;

    private static final float CAMERA_POS_STEP = 0.05f;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        BlockTypes.register();
        Biomes.register();
        world = WorldFactory.generate();
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        File f = new File("world.xml");
        f.createNewFile();
        outputter.output(world.toXML(), new FileWriter(f));

        renderer.init(window);

        Texture texture = new Texture("/textures/grass.png");
        Mesh mesh = new Mesh(BlockUtils.positions, BlockUtils.textCoords, BlockUtils.indices, texture);

        GameItem gameItem1 = new GameItem(mesh);
        gameItem1.setScale(0.5f);
        gameItem1.setPosition(0, 0, -2);

        GameItem gameItem2 = new GameItem(mesh);
        gameItem2.setScale(0.5f);
        gameItem2.setPosition(0.5f, 0.5f, -2);

        GameItem gameItem3 = new GameItem(mesh);
        gameItem3.setScale(0.5f);
        gameItem3.setPosition(0, 0, -2.5f);

        GameItem gameItem4 = new GameItem(mesh);
        gameItem4.setScale(0.5f);
        gameItem4.setPosition(0.5f, 0, -2.5f);

        gameItems = new GameItem[]{gameItem1, gameItem2, gameItem3, gameItem4};
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse            
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}