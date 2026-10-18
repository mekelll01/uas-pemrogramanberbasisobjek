package absensi.app;

import absensi.view.MenuAwalView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class MainApp extends Application {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setTitle("Sistem Absensi Mahasiswa");
        stage.setMaximized(true);
        stage.show();

        MenuAwalView menu = new MenuAwalView(root);
        root.getChildren().add(menu.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
