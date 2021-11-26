package sql;

import httpclient.MovieApiDownloadInfo;
import httpclient.model.Movie;

import java.io.IOException;
import java.sql.SQLException;

public class WriteInDB {
    public static void main(String[] args) throws IOException, SQLException {
        int countPages = MovieApiDownloadInfo.countPages();
        for (int i = 1; i <= countPages; i++) {
            Movie [] movies = MovieApiDownloadInfo.getArrayOfFilmsFromPages(i);
            for (int j =0; j<movies.length;j++){
                JdbcExample.writeInDB(movies[j]);
            }
        }
    }

}