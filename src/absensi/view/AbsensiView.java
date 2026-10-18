package absensi.view;

import absensi.DeteksiWajah;
import absensi.dao.UserDAO;
import absensi.dao.AbsensiDAO;
import absensi.model.User;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.ByteArrayInputStream;
import java.io.File;

public class AbsensiView {

    private final StackPane root;
    private ImageView cameraView;
    private TextField txtNama;
    private TextField txtNim;
    private DeteksiWajah deteksiWajah;
    private AnimationTimer timer;
    private Mat lastFrame;

    public AbsensiView(StackPane root) {
        this.root = root;
    }

    public Parent getView() {

        Label title = new Label("Absensi Mahasiswa");
        title.setStyle("-fx-font-size:26px; -fx-font-weight:bold;");

        Button btnBack = new Button("← Kembali");
        btnBack.setStyle("-fx-background-color:#3498db; -fx-text-fill:white; -fx-background-radius:8;");
        btnBack.setOnAction(e -> {
            stopCamera();
            root.getChildren().setAll(new MenuAwalView(root).getView());
        });

        HBox header = new HBox(15, btnBack, title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));

        cameraView = new ImageView();
        cameraView.setPreserveRatio(true);
        cameraView.setSmooth(true);

        StackPane cameraPane = new StackPane(cameraView);
        cameraPane.setPrefHeight(520);
        cameraPane.setStyle("-fx-background-color:black; -fx-background-radius:18;");

        cameraView.fitWidthProperty().bind(cameraPane.widthProperty());
        cameraView.fitHeightProperty().bind(cameraPane.heightProperty());

        txtNama = createBigField("Nama Lengkap");
        txtNim = createBigField("NIM");

        Button btnSimpan = new Button("Simpan Absensi");
        btnSimpan.setPrefHeight(48);
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        btnSimpan.setStyle("-fx-background-color:#3498db; -fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold; -fx-background-radius:10;");
        btnSimpan.setOnAction(e -> save());

        VBox form = new VBox(16, txtNama, txtNim, btnSimpan);
        form.setPadding(new Insets(30));
        form.setMaxWidth(620);
        form.setStyle("-fx-background-color:white; -fx-background-radius:18; -fx-effect:dropshadow(gaussian, rgba(0,0,0,0.2), 18,0,0,6);");

        VBox center = new VBox(30, cameraPane, form);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(20));
        VBox.setVgrow(cameraPane, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(center);
        layout.setStyle("-fx-background-color:#ecf0f1;");

        startCamera();
        return layout;
    }

    private TextField createBigField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setPrefHeight(48);
        tf.setStyle("-fx-font-size:14px; -fx-background-radius:10; -fx-padding:12 14 12 14;");
        return tf;
    }

    private void startCamera() {
        new Thread(() -> {
            deteksiWajah = new DeteksiWajah();
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    Mat frame = deteksiWajah.ambilFrame();
                    if (frame == null || frame.empty()) return;
                    lastFrame = deteksiWajah.ambilFrameAsli();
                    cameraView.setImage(mat2Image(frame));
                }
            };
            javafx.application.Platform.runLater(timer::start);
        }).start();
    }

    private void stopCamera() {
        if (timer != null) timer.stop();
        if (deteksiWajah != null) deteksiWajah.stop();
    }

    private void save() {

        if (lastFrame == null) {
            showError("Kamera belum siap");
            return;
        }

        String nama = txtNama.getText().trim();
        String nim = txtNim.getText().trim();

        if (nama.isEmpty() || nim.isEmpty()) {
            showError("Nama dan NIM wajib diisi");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User u = userDAO.getByNim(nim);

        if (u == null) {
            new File("faces").mkdirs();
            String path = "faces/" + nim + ".png";
            Imgcodecs.imwrite(path, lastFrame);

            u = new User();
            u.setNama(nama);
            u.setNim(nim);
            u.setFotoPath(path);
            userDAO.insert(u);
        }

        AbsensiDAO absensiDAO = new AbsensiDAO();
        if (absensiDAO.sudahAbsenHariIni(u.getId())) {
            showError("Anda sudah absen hari ini!");
            return;
        }

        try {
            absensiDAO.insert(u.getId());
            txtNama.clear();
            txtNim.clear();
            showPopup();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal menyimpan ke database:\n" + e.getMessage());
        }
    }

    private void showPopup() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");

        Label icon = new Label("✓");
        icon.setStyle("-fx-font-size:42px; -fx-text-fill:#2ecc71;");

        Label title = new Label("Absensi Berhasil");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        Label sub = new Label("Data & foto tersimpan");
        sub.setStyle("-fx-text-fill:#7f8c8d;");

        VBox card = new VBox(8, icon, title, sub);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color:white; -fx-background-radius:18;");

        overlay.getChildren().add(card);
        root.getChildren().add(overlay);

        PauseTransition wait = new PauseTransition(Duration.seconds(2));
        wait.setOnFinished(e -> root.getChildren().remove(overlay));
        wait.play();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private Image mat2Image(Mat m) {
        MatOfByte b = new MatOfByte();
        Imgcodecs.imencode(".png", m, b);
        return new Image(new ByteArrayInputStream(b.toArray()));
    }
}
