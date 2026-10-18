package absensi.view;

import absensi.dao.AdminDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final StackPane root;

    public LoginView(StackPane root) {
        this.root = root;
    }

    public Parent getView() {

        Label title = new Label("Login Admin");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 6 14;");

        btnLogin.setOnAction(e -> {
            String username = tfUser.getText().trim();
            String password = pfPass.getText().trim();
            if (new AdminDAO().login(username, password)) {
                root.getChildren().setAll(new AdminDashboardView(root).getView());
            } else {
                lblError.setText("Username atau password salah!");
            }
        });

        VBox formCard = new VBox(15, title, tfUser, pfPass, lblError, btnLogin);
        formCard.setAlignment(Pos.CENTER);
        formCard.setPadding(new Insets(25));
        formCard.setPrefWidth(360);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10,0,0,4);");

        Button btnKembali = new Button("← Kembali");
        btnKembali.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 6 14;");
        btnKembali.setOnAction(e -> root.getChildren().setAll(new MenuAwalView(root).getView()));

        StackPane wrapper = new StackPane(formCard, btnKembali);
        StackPane.setAlignment(formCard, Pos.CENTER);
        StackPane.setAlignment(btnKembali, Pos.TOP_LEFT);
        StackPane.setMargin(btnKembali, new Insets(20));
        wrapper.setStyle("-fx-background-color: rgba(0,0,0,0.35);");

        return wrapper;
    }
}
