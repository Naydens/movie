package sql;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import httpclient.model.Movie;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.List;

public class JdbcExample {
    static Connection connection;
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost/moviel?"
                            + "user=root&password=Qwerty123");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    static public void writeInDB(Movie movie) throws SQLException {
        Statement statement = connection.createStatement();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO Movies VALUES (")
                .append(""+movie.getId() + ",")
                .append("'"+movie.getPosterPath() + "',")
                .append(""+getBoolean(movie.isAdult()) + ",")
                .append("'"+deleteWrongSymbols(movie.getOverview()) + "',")
                .append("NULLIF('"+(movie.getReleaseDate()==null?"":movie.getReleaseDate())+ "',''),")
                .append("'"+getArray(movie.getGenreIds())+"',")
                .append("'"+deleteWrongSymbols(movie.getOriginalTitle())+"',")
                .append("'"+movie.getOriginalLanguage()+"',")
                .append("'"+deleteWrongSymbols(movie.getTitle())+"',")
                .append("'"+movie.getBackdropPath()+"',")
                .append(""+movie.getPopularity()+",")
                .append(""+movie.getVoteCount()+",")
                .append(""+getBoolean(movie.isVideo())+",")
                .append(""+movie.getVoteAverage()+");");
        try {
            statement.executeUpdate(stringBuilder.toString());
        }
        catch (SQLSyntaxErrorException |MysqlDataTruncation e){
            System.out.println(getArray(movie.getGenreIds()).length());
            System.out.println(e.getMessage());
        }

    }

    static int getBoolean(boolean value) {
        if (value) {
            return 1;
        }
        return 0;
    }

    static String getArray(List<Integer> list) {
        return StringUtils.join(list, ",");
    }

    static String deleteWrongSymbols(String s){
        return s.replaceAll("\b","\\\\b")
                .replaceAll("\n","\\\\n")
                .replaceAll("\r", "\\\\r")
                .replaceAll("\t", "\\\\t")
                .replaceAll("\\x1A", "\\\\Z")
                .replaceAll("\\x00", "\\\\0")
                .replaceAll("'", "\\\\'")
                .replaceAll("\"", "\\\"");
    }

}
