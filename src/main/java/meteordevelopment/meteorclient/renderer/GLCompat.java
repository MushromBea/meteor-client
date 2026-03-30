package meteordevelopment.meteorclient.renderer;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * GL compatibility layer for environments where standard desktop GL
 * calls are unavailable or translated (e.g., PojavLauncher with gl4es).
 */
public final class GLCompat {
    private static boolean isPojavLauncher = false;
    private static boolean vaoSupported = true;
    private static boolean fboSupported = true;
    private static boolean checked = false;

    private GLCompat() {}

    public static void init() {
        if (checked) return;
        checked = true;

        // Detect PojavLauncher by checking for its characteristic system property
        String renderer = System.getProperty("gl.renderer", "");
        String pojav = System.getProperty("pojav.path", null);
        if (pojav != null) {
            isPojavLauncher = true;
        }

        // Also detect by checking environment variable
        String env = System.getenv("POJAV_ENVIRON");
        if (env != null) {
            isPojavLauncher = true;
        }

        // Detect by class presence
        try {
            Class.forName("net.kdt.pojavlaunch.utils.JREUtils");
            isPojavLauncher = true;
        } catch (ClassNotFoundException ignored) {}

        if (isPojavLauncher) {
            // Test VAO support
            try {
                int testVao = GL30C.glGenVertexArrays();
                GL30C.glDeleteVertexArrays(testVao);
                vaoSupported = true;
            } catch (Throwable t) {
                vaoSupported = false;
            }

            // Test FBO support
            try {
                int testFbo = GL30C.glGenFramebuffers();
                GL30C.glDeleteFramebuffers(testFbo);
                fboSupported = true;
            } catch (Throwable t) {
                fboSupported = false;
            }
        }
    }

    public static boolean isPojavLauncher() {
        if (!checked) init();
        return isPojavLauncher;
    }

    public static boolean isVaoSupported() {
        if (!checked) init();
        return vaoSupported;
    }

    public static boolean isFboSupported() {
        if (!checked) init();
        return fboSupported;
    }
}
