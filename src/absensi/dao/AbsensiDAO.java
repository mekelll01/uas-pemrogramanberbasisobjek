package absensi.dao;

import absensi.db.KoneksiDB;
import absensi.model.Absensi;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AbsensiDAO {

    public boolean sudahAbsenHariIni(int userId) {
        String sql = "SELECT 1 FROM absensi WHERE user_id=? AND tanggal=?";
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(int userId) {
        if (sudahAbsenHariIni(userId)) {
            return false;
        }

        String sql = "INSERT INTO absensi (user_id, tanggal, waktu) VALUES (?, ?, ?)";
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setTime(3, Time.valueOf(LocalTime.now()));
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Absensi> getAllAbsensi() {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT a.id, a.user_id, a.tanggal, a.waktu, u.nama, u.nim " +
                "FROM absensi a " +
                "JOIN user u ON a.user_id = u.id " +
                "ORDER BY u.nama ASC, u.nim ASC, a.tanggal DESC";

        try (Connection conn = KoneksiDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while(rs.next()) {
                Absensi a = new Absensi();
                a.setId(rs.getInt("id"));
                a.setUserId(rs.getInt("user_id"));
                a.setTanggal(rs.getDate("tanggal").toLocalDate());
                a.setWaktu(rs.getTime("waktu").toLocalTime());
                a.setNama(rs.getString("nama"));
                a.setNim(rs.getString("nim"));
                list.add(a);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
