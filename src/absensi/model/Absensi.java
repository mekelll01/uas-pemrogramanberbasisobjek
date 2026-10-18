package absensi.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Absensi {

    private int id;
    private int userId;
    private LocalDate tanggal;
    private LocalTime waktu;
    private String nama;
    private String nim;

    public Absensi() { }

    public Absensi(int id, int userId, LocalDate tanggal, LocalTime waktu, String nama, String nim) {
        this.id = id;
        this.userId = userId;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.nama = nama;
        this.nim = nim;
    }

    public Absensi(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getWaktu() {
        return waktu;
    }

    public void setWaktu(LocalTime waktu) {
        this.waktu = waktu;
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
}
