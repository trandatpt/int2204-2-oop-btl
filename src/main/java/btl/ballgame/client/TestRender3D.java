//package btl.ballgame.client;
//
//import java.awt.Canvas;
//import java.io.File;
//
//import javafx.scene.paint.Color;
//import btl.ballgame.client.ui.window.Window;
//import btl.ballgame.client.ui.window.WindowManager;
//import javafx.animation.Animation;
//import javafx.animation.RotateTransition;
//import javafx.geometry.Pos;
//import javafx.scene.AmbientLight;
//import javafx.scene.Group;
//import javafx.scene.PerspectiveCamera;
//import javafx.scene.SceneAntialiasing;
//import javafx.scene.SubScene;
//import javafx.scene.image.Image;
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.PhongMaterial;
//import javafx.scene.shape.Box;
//import javafx.scene.shape.CullFace;
//import javafx.scene.transform.Rotate;
//import javafx.util.Duration;
//
//public class TestRender3D extends Window {
//	public static PerspectiveCamera cam;
//	public static Rotate camXRot = new Rotate(0, Rotate.X_AXIS);
//	public static Rotate camYRot = new Rotate(0, Rotate.Y_AXIS);
//	
//	private SubScene subScene;
//	private Group root;
//	private Box panorama;
//	
//	public TestRender3D(WindowManager manager) {
//		super();
//		this.setTitle("balls");
//		this.setWindowId("ball");
//		initUI();
//	}
//
//	@Override
//	public void initUI() {
//		int w = 1280, h = 720;
//		
//		this.panorama = new Box(1000, 1000, 1000);
//		this.panorama.setCullFace(CullFace.NONE);
//		this.panorama.setScaleX(1);
//		
//		PhongMaterial phong = new PhongMaterial();
//		phong.setDiffuseMap(new Image(new File("C:\\Users\\GiaKhanhVN\\Pictures\\God of War\\test.png").toURI().toString()));
//		this.panorama.setMaterial(phong);
//		AmbientLight light = new AmbientLight(Color.WHITE);
//
//		this.root = new Group(this.panorama, light);
//		
//		PerspectiveCamera camera = new PerspectiveCamera(true);
//		camera.setNearClip(0.1);
//		camera.setFarClip(100_000);
//		camera.getTransforms().addAll(camYRot, camXRot);
//		cam = camera;
//		
//		this.subScene = new SubScene(root, w, h, true, SceneAntialiasing.BALANCED);
//		this.subScene.setCamera(camera);
//		
//		//GaussianBlur gauss = new GaussianBlur(3);
//		//subScene.setEffect(gauss);
//		
//		RotateTransition spin = new RotateTransition(Duration.seconds(20), this.panorama);
//		spin.setAxis(Rotate.Y_AXIS);
//		spin.setFromAngle(0);
//		spin.setToAngle(360);
//		spin.setCycleCount(Animation.INDEFINITE);
//		spin.play();
//		
//		StackPane a = new StackPane(subScene);
//		a.setAlignment(Pos.CENTER);
//		getChildren().add(a);
//	}
//	
//}
