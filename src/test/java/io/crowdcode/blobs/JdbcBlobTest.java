package io.crowdcode.blobs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import static org.junit.Assert.assertTrue;

/**
 * Simple Test passing a BLOB stream to a PLSQL Procedure
 */
public class JdbcBlobTest {

    public static final int SIZE = 1024 * 1024 *5;
    public static final String JDBC_URL = "jdbc:oracle:thin:@entw-ep:51521:XE";
    public static final String JDBC_USER = "ep_entw";
    public static final String JDBC_PASS = "entw_geheim";
    public static final boolean USE_AUTOCOMMIT = true;
    Connection connection = null;

     String random = "";

     @Before
     public void setup() throws ClassNotFoundException, SQLException {
         System.out.println("OPEN CONNECTION");
         Class.forName("oracle.jdbc.driver.OracleDriver");
         connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
         connection.setAutoCommit(USE_AUTOCOMMIT);

         StringBuilder b =new StringBuilder(SIZE);
         System.out.println("CREATE RANDOM STRING");
         for (int i = 0; i< SIZE; ++i){
             b.append(String.valueOf(Double.valueOf(Math.random()).intValue()%10));
             if ((i % 10000) == 0) {
                 System.out.println("RANDOM VALUES "+i);
             }
         }
         random = b.toString();
         System.out.println("DONE");
     }


     @After
     public void after() throws SQLException {
         connection.close();
     }

    @Test
    public void testBlobAccess() throws SQLException, IOException, InterruptedException {

         // 1. CREATE ACCESS TO LOB FUNCTION
        String plsql="DECLARE BEGIN InsertBlob.TestBlobInsert(:myid, :filedata); END; ";
        InputStream inputStream = new ByteArrayInputStream(random.getBytes());
        CallableStatement statement = connection.prepareCall(plsql);
        try {
            // 2. CREATE INPUT STREAM
            statement.setInt("myid", 4711);
            statement.setBinaryStream("filedata", inputStream, random.getBytes().length);
            statement.executeUpdate();

            verifyBlob();
        } finally {
            if (!USE_AUTOCOMMIT) {
                connection.commit();
            }
            inputStream.close();
            statement.close();
        }
    }

    private void verifyBlob() throws SQLException {
        int i = 0;
        PreparedStatement stmt = connection.prepareStatement("SELECT rownum, dbms_lob.getlength(blobdata) FROM BlobTable");
        stmt.executeQuery();
        ResultSet resultSet = stmt.getResultSet();

        while(resultSet.next()) {
            System.out.println("ROW "+ ++i);
            int anInt = resultSet.getInt(1);
            assertTrue(anInt > 0);
        }

        resultSet.close();
    }
}
