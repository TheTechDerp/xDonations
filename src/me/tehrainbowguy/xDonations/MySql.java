package me.tehrainbowguy.xDonations;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;


public class MySql {

    static FileConfiguration config = xDonations.config;
    static String url = config.getString("xp.config.database");
    static String user = config.getString("xp.config.user");
    static String pass = config.getString("xp.config.password");
    static Connection conn = null;
    public static void initDB() throws SQLException {
        conn = DriverManager.getConnection(url, user, pass); //Creates the connection
    }
    public static void closeConn() throws SQLException {
        conn.close();
    }

    public static void createTables() throws SQLException {
        PreparedStatement Statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `xDonations` (" +
                "  `ID` int(11) NOT NULL auto_increment," +
                "  `Player` varchar(16) NOT NULL," +
                "  `DonationAmt` double NOT NULL," +
                "  PRIMARY KEY  (`ID`)," +
                "  UNIQUE KEY `Player` (`Player`)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
        Statement.executeUpdate(); //Executes the query
        Statement.close(); //Closes the query
    }

    public static void setBalance(String p, double i) throws SQLException {
        PreparedStatement Statement = conn.prepareStatement("UPDATE `xDonations` SET DonationAmt='" + i + "' WHERE Player LIKE'" + p + "';");
        Statement.executeUpdate(); //Executes the query
        Statement.close(); //Closes the query
    }

    public static double getBalance(String p) throws SQLException {
        createUser(p);
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("SELECT DonationAmt FROM `xDonations` WHERE Player LIKE '" + p + "';");
        double result = 0;
        if (rs.next()) {
            result = rs.getDouble("DonationAmt");
        }
        state.close();
        return result;
    }

    private static ArrayList<String> seen = new ArrayList<String>();
    public static void createUser(String player) throws SQLException {
        if(seen.contains(player)){return;}else{seen.add(player);} //Basic cache!
        Statement state = conn.createStatement();
        final ResultSet rs = state.executeQuery("SELECT * FROM `xDonations` WHERE Player LIKE '" + player+ "';");
        if (!rs.first()) {
            PreparedStatement Statement1 = conn.prepareStatement("INSERT INTO `xDonations` (`ID`, `Player`, `DonationAmt`) VALUES (NULL, '" + player + "', '0');"); //Put your query in the quotes
            Statement1.executeUpdate();
            Statement1.close();
            //INSERT INTO `xDonations` (`id`, `User`, `Balance`) VALUES (NULL, 'TehRainbowGuy', '0');
        }
        state.close();

    }

}
