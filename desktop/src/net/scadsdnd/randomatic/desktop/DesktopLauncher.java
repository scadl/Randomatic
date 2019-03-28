package net.scadsdnd.randomatic.desktop;

//import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.scadsdnd.randomatic.RandomaticMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new RandomaticMain(), config);
		//config.addIcon("build/icon.png", Files.FileType.Internal);
	}
}
