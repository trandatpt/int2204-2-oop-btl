package btl.ballgame.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import btl.ballgame.shared.libs.external.Json;

public class DataManager {
	private File fileRoot;
	private File userDataRoot;
	private File spFile;
	
	public DataManager() {
		this.fileRoot = new File("data");
		this.userDataRoot = new File(this.fileRoot, "users");
		if (!this.userDataRoot.exists()) {
			this.userDataRoot.mkdirs();
		}
		this.spFile = new File(this.fileRoot, "server.json");
	}
	
	public Json getServerProperties() throws IOException {
		Json data;
		System.out.println(spFile.toPath());
		if (!spFile.exists()) {
			// default config values
			data = Json.object()
				.set("tcp-port", 3636)
				.set("max-players", 120)
				.set("server-name", "An Arkanoid Server")
			;
			Files.writeString(spFile.toPath(), data.toString());
			return data;
		}
		// read the properties
		return Json.read(Files.readString(spFile.toPath()));
 	}
}
