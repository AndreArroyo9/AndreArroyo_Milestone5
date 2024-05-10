import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PicturesManager {
    // JDBC driver name and database
    static final String SERVER_IP = "localhost";
    static final String DB_NAME = "photography";
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://" + SERVER_IP + ":3308/" + DB_NAME;

    // Database credentials
    static final String USER = "root";
    static final String PASS = "root";
    private Connection conn;
    public PicturesManager(){
        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected.");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            conn.close();
            System.out.println("Disconnected form db.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Photographer[] photographers(){
        Photographer[] photographerArray;
        PreparedStatement select;
        try{
            select = conn.prepareStatement("SELECT * FROM Photographers;");
            ResultSet rs = select.executeQuery();

            // It moves to the last row, get the number of rows in the ResultSet
            // and comes back to the before first position.
            rs.last();
            int photographersLength = rs.getRow();
            rs.beforeFirst();

            photographerArray = new Photographer[photographersLength];
            int i = 0;

            while (rs.next()){
                int photographerId = rs.getInt("PhotographerId");
                String name = rs.getString("Name");
                Boolean awarded = rs.getBoolean("Awarded");

                Photographer photographer = new Photographer(photographerId, name, awarded);

                photographerArray[i] = photographer;
                i++;
            }
            select.close();
            rs.close();
            return photographerArray;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Picture> pictures(Photographer photographer, Date date){
        List<Picture> pictures = new ArrayList<>();
        PreparedStatement select;
        try {
            if (date != null) {
                select = conn.prepareStatement("SELECT * FROM pictures WHERE Photographer = ? AND DatePicture >= ?;");
                select.setInt(1, photographer.getPhotographerId());
                select.setDate(2, date);
            }else {
                select = conn.prepareStatement("SELECT * FROM pictures WHERE Photographer = ?;");
                select.setInt(1, photographer.getPhotographerId());
            }
                ResultSet rs = select.executeQuery();

            while (rs.next()) {
                int pictureId = rs.getInt("PictureId");
                String title = rs.getString("Title");
                Date datePicture = rs.getDate("DatePicture");
                String file = rs.getString("File");
                int visits = rs.getInt("Visits");

                Picture picture = new Picture(pictureId, title, date, file, visits, photographer);
                pictures.add(picture);
            }
            select.close();
            rs.close();

            return pictures;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void incrementVisits(Picture picture){
        PreparedStatement update;
        try{
            update = conn.prepareStatement("UPDATE Pictures SET Visits = Visits + 1 WHERE PictureId = ?;");
            update.setInt(1, picture.getPictureId());
            update.executeUpdate();
            update.close();
        }catch (SQLException e){
            System.err.println("ERROR UPDATING");
            e.printStackTrace();
        }
    }

    public HashMap<Integer, Integer> createVisitsMap(){
        List<Picture> pictures = new ArrayList<>();
        PreparedStatement select;
        HashMap<Integer, Integer> visitsMap = new HashMap<>();
        try {
            select = conn.prepareStatement("SELECT Photographer, Visits FROM pictures;");
            ResultSet rs = select.executeQuery();

            int lastVisits = 0;
            while (rs.next()) {
                int photographerId = rs.getInt("Photographer");
                int visits = rs.getInt("Visits");

                if (visitsMap.containsKey(photographerId)){
                    visits+=lastVisits;
                }
                visitsMap.put(photographerId,visits);
                lastVisits= visits;
            }
            select.close();
            rs.close();

            return visitsMap;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public void awardPhotographers(int minVisits){
        PreparedStatement update;
        Photographer[] photographers = photographers();
        createVisitsMap().forEach((photographerID, visits) ->
        {
            if (visits>=minVisits) {
                awardPhotographerDB(photographerID);
            }
        });
        
    }

    public void awardPhotographerDB(int photographerID){
        PreparedStatement update;
        try{
            update = conn.prepareStatement("UPDATE photographers SET Awarded = true WHERE PhotographerId = ?;");
            update.setInt(1, photographerID);
            update.executeUpdate();
            update.close();
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Something went wrong trying to update the photographer with id " + photographerID, "Alert", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PicturesManager pc = new PicturesManager();
        HashMap<Integer,Integer> hashMap = pc.createVisitsMap();
        hashMap.forEach(
                (k,v) -> System.out.println("ID:" + k + " Visits: " + v)
        );
    }
}
