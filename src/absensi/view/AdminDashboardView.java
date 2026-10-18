package absensi.view;

import absensi.dao.UserDAO;
import absensi.model.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class AdminDashboardView {

    private final StackPane root;
    private TableView<User> table;

    public AdminDashboardView(StackPane root) {
        this.root = root;
    }

    public Parent getView() {

        Label title = new Label("Dashboard Admin");
        title.setFont(Font.font(24));

        Button btnBack = new Button("← Kembali");
        btnBack.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 6 14;");
        btnBack.setOnAction(e ->
                root.getChildren().setAll(new MenuAwalView(root).getView())
        );

        Button btnCari = new Button("Cari");
        btnCari.setStyle("-fx-background-color:#2ecc71;-fx-text-fill:white;-fx-background-radius:8;");
        btnCari.setOnAction(e -> showCariPopup());

        Button btnTampilSemua = new Button("Tampilkan Semua Data");
        btnTampilSemua.setStyle("-fx-background-color:#f39c12;-fx-text-fill:white;-fx-background-radius:8;");
        btnTampilSemua.setOnAction(e -> refreshTable());

        HBox headerLeft = new HBox(15, btnBack, title);
        headerLeft.setAlignment(Pos.CENTER_LEFT);

        HBox headerRight = new HBox(12, btnCari, btnTampilSemua);
        headerRight.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox();
        HBox.setHgrow(headerLeft, Priority.ALWAYS);
        header.getChildren().addAll(headerLeft, headerRight);
        header.setPadding(new Insets(25));
        header.setAlignment(Pos.CENTER);

        table = new TableView<>();
        table.setFixedCellSize(160);
        table.setStyle("-fx-font-size:14px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, ImageView> colFoto = new TableColumn<>("Foto");
        colFoto.setPrefWidth(240);
        colFoto.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getFoto()));
        colFoto.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    item.setFitWidth(200);
                    item.setFitHeight(140);
                    item.setPreserveRatio(true);
                    setGraphic(new StackPane(item));
                }
            }
        });

        TableColumn<User, String> colNama = new TableColumn<>("Nama");
        colNama.setPrefWidth(320);
        colNama.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNama()));
        colNama.setStyle("-fx-alignment:CENTER;");

        TableColumn<User, String> colNim = new TableColumn<>("NIM");
        colNim.setPrefWidth(220);
        colNim.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNim()));
        colNim.setStyle("-fx-alignment:CENTER;");

        TableColumn<User, String> colWaktu = new TableColumn<>("Waktu Absensi");
        colWaktu.setPrefWidth(220);
        colWaktu.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getWaktuAbsensi()));
        colWaktu.setStyle("-fx-alignment:CENTER;");

        TableColumn<User, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(220);
        colAksi.setCellFactory(tc -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnHapus = new Button("Hapus");
            private final HBox box = new HBox(12, btnEdit, btnHapus);

            {
                box.setAlignment(Pos.CENTER);
                btnEdit.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;-fx-background-radius:8;-fx-padding:6 14;");
                btnHapus.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:8;-fx-padding:6 14;");

                btnEdit.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    showEditDialog(u);
                });

                btnHapus.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    showDeleteConfirm(u);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().setAll(colFoto, colNama, colNim, colWaktu, colAksi);
        refreshTable();

        VBox card = new VBox(table);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color:white;-fx-background-radius:20;-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.18), 18,0,0,6);");

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(card);
        layout.setStyle("-fx-background-color:#ecf0f1;");

        return layout;
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(new UserDAO().getAll()));
    }

    private void showCariPopup() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Cari Data Mahasiswa");

        ButtonType cariBtn = new ButtonType("Cari", ButtonBar.ButtonData.OK_DONE);
        ButtonType batalBtn = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cariBtn, batalBtn);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        TextField txtCari = new TextField();
        txtCari.setPromptText("Masukkan Nama atau NIM");
        content.getChildren().add(txtCari);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == cariBtn) {
                String key = txtCari.getText().trim();
                table.setItems(FXCollections.observableArrayList(
                        new UserDAO().getAll().stream()
                                .filter(u -> u.getNama().toLowerCase().contains(key.toLowerCase())
                                        || u.getNim().toLowerCase().contains(key.toLowerCase()))
                                .toList()
                ));
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showDeleteConfirm(User user) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.35);");

        Label title = new Label("Hapus Data?");
        title.setStyle("-fx-font-size:22px; -fx-font-weight:bold;");
        Label sub = new Label("Data ini akan dihapus permanen");
        sub.setStyle("-fx-text-fill:#7f8c8d;");

        Button btnCancel = new Button("Batal");
        Button btnDelete = new Button("Hapus");
        btnCancel.setStyle("-fx-background-color:#bdc3c7;-fx-background-radius:8;-fx-padding:8 20;");
        btnDelete.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:8;-fx-padding:8 20;");

        btnCancel.setOnAction(e -> root.getChildren().remove(overlay));
        btnDelete.setOnAction(e -> {
            new UserDAO().delete(user.getId());
            refreshTable();
            root.getChildren().remove(overlay);
        });

        HBox actions = new HBox(12, btnCancel, btnDelete);
        actions.setAlignment(Pos.CENTER);

        VBox card = new VBox(10, title, sub, actions);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26));
        card.setPrefWidth(360);
        card.setPrefHeight(180);
        card.setStyle("-fx-background-color:white;-fx-background-radius:18;-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.25), 20,0,0,6);");

        overlay.getChildren().add(card);
        root.getChildren().add(overlay);
    }

    private void showEditDialog(User user) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.35);");

        Label title = new Label("Edit Data Mahasiswa");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        TextField tfNama = new TextField(user.getNama());
        TextField tfNim = new TextField(user.getNim());
        tfNama.setPrefHeight(36);
        tfNim.setPrefHeight(36);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Nama"), 0, 0);
        form.add(tfNama, 1, 0);
        form.add(new Label("NIM"), 0, 1);
        form.add(tfNim, 1, 1);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPrefWidth(60);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c1, c2);

        Button btnBatal = new Button("Batal");
        Button btnSimpan = new Button("Simpan");
        btnBatal.setStyle("-fx-background-color:#bdc3c7;-fx-background-radius:8;-fx-padding:6 18;");
        btnSimpan.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;-fx-background-radius:8;-fx-padding:6 18;");

        btnBatal.setOnAction(e -> root.getChildren().remove(overlay));
        btnSimpan.setOnAction(e -> {
            user.setNama(tfNama.getText());
            user.setNim(tfNim.getText());
            new UserDAO().update(user);
            refreshTable();
            root.getChildren().remove(overlay);
        });

        HBox actions = new HBox(10, btnBatal, btnSimpan);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(14, title, form, actions);
        card.setPadding(new Insets(22));
        card.setPrefWidth(420);
        card.setPrefHeight(230);
        card.setStyle("-fx-background-color:white;-fx-background-radius:18;-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.25), 20,0,0,6);");

        overlay.getChildren().add(card);
        root.getChildren().add(overlay);
    }
}
