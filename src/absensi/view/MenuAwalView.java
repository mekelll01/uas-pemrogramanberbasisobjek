package absensi.view;

import absensi.dao.AdminDAO;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MenuAwalView {

    private final StackPane root;

    public MenuAwalView(StackPane root) {
        this.root = root;
    }

    public Parent getView() {

        Label title = new Label("Selamat Datang");
        title.setStyle("""
                -fx-font-size: 26px;
                -fx-font-weight: bold;
                -fx-text-fill: #2c3e50;
                """);

        Label subtitle = new Label("Silakan pilih menu");
        subtitle.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #7f8c8d;
                """);

        Button btnAbsen = createButton("Absen Kehadiran", "#3498db");
        Button btnAdmin = createButton("Dashboard Admin", "#2ecc71");

        btnAbsen.setOnAction(e -> {
            root.getChildren().clear();
            AbsensiView absensi = new AbsensiView(root);
            root.getChildren().add(absensi.getView());
        });

        btnAdmin.setOnAction(e -> root.getChildren().setAll(new LoginView(root).getView()));

        VBox card = new VBox(15, title, subtitle, btnAbsen, btnAdmin);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 14;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 6);
                """);

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #ecf0f1;");

        playIntroAnimation(card);

        return wrapper;
    }

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(280, 50);
        btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 10;
                """.formatted(color));

        ScaleTransition in = new ScaleTransition(Duration.millis(150), btn);
        in.setToX(1.05);
        in.setToY(1.05);

        ScaleTransition out = new ScaleTransition(Duration.millis(150), btn);
        out.setToX(1);
        out.setToY(1);

        btn.setOnMouseEntered(e -> in.playFromStart());
        btn.setOnMouseExited(e -> out.playFromStart());

        return btn;
    }

    private void playIntroAnimation(VBox card) {
        card.setOpacity(0);
        card.setTranslateY(40);

        FadeTransition fade = new FadeTransition(Duration.millis(500), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(500), card);
        slide.setFromY(40);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();
    }
}
