package absensi.dao;

import absensi.db.KoneksiDB;
import absensi.model.User;
import absensi.model.Absensi;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void insert(User user) {
        String sql = "INSERT INTO user (nama, nim, foto_path, created_at) VALUES (?, ?, ?, NOW())";

        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getNama());
            ps.setString(2, user.getNim());
            ps.setString(3, user.getFotoPath());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        String sql = "UPDATE user SET nama=?, nim=? WHERE id=?";

        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getNama());
            ps.setString(2, user.getNim());
            ps.setInt(3, user.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sqlAbsensi = "DELETE FROM absensi WHERE user_id=?";
        String sqlUser = "DELETE FROM user WHERE id=?";

        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement psAbs = c.prepareStatement(sqlAbsensi);
             PreparedStatement psUser = c.prepareStatement(sqlUser)) {

            psAbs.setInt(1, id);
            psAbs.executeUpdate();

            psUser.setInt(1, id);
            psUser.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getByNim(String nim) {
        String sql = "SELECT * FROM user WHERE nim=?";
        User u = null;

        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nim);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = new User(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("nim"),
                        rs.getString("foto_path"),
                        rs.getString("created_at")
                );
                u.setFoto(createImageView(u.getFotoPath()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return u;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY id DESC";

        try (Connection c = KoneksiDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("nim"),
                        rs.getString("foto_path"),
                        rs.getString("created_at")
                );
                u.setFoto(createImageView(u.getFotoPath()));
                list.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Absensi> getAbsensiByUser(int userId) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE user_id=? ORDER BY tanggal DESC";

        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Absensi a = new Absensi();
                a.setId(rs.getInt("id"));
                a.setUserId(rs.getInt("user_id"));
                a.setTanggal(rs.getDate("tanggal").toLocalDate());
                a.setWaktu(rs.getTime("waktu").toLocalTime());
                list.add(a);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    private ImageView createImageView(String path) {
        Image img = (path != null && !path.isEmpty())
                ? new Image("file:" + path, 200, 140, true, true)
                : new Image("file:faces/default.jpg", 200, 140, true, true);

        ImageView iv = new ImageView(img);
        iv.setFitWidth(200);
        iv.setFitHeight(140);
        iv.setPreserveRatio(true);
        return iv;
    }
}
