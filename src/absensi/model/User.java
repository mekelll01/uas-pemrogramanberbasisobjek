package absensi.model;

import javafx.scene.image.ImageView;

public class User {

    private int id;
    private String nama;
    private String nim;
    private String fotoPath;
    private ImageView foto;
    private String waktuAbsensi;

    public User() {}

    public User(int id, String nama, String nim, String fotoPath, String waktuAbsensi) {
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.fotoPath = fotoPath;
        this.waktuAbsensi = waktuAbsensi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public ImageView getFoto() {
        return foto;
    }

    public void setFoto(ImageView foto) {
        this.foto = foto;
    }

    public String getWaktuAbsensi() {
        return waktuAbsensi;
    }

    public void setWaktuAbsensi(String waktuAbsensi) {
        this.waktuAbsensi = waktuAbsensi;
    }
}
