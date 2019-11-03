package com.iropensource.dbcp;

import com.iropensource.model.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Launcher {
    public static final int CONCURRENT_INT = 10;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // دیتاسورس ارتباط با دیتابیس را برام بساز
        DataSource dataSource = DataSourceSingleton.getInstance();

        // حال اینکه این کتابخانه چطور کار میکند را با ۱۰ تا ترد شبیه سازی میکنیم
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_INT);


        List<Future<List<User>>> list = new ArrayList<Future<List<User>>>();
        for (int i = 0; i < CONCURRENT_INT; i++) {
            Future<List<User>> future = executor.submit(new UserCallable(dataSource));
            list.add(future);
        }

        //یدونه رو برای نمایش چاپ کن
        printFirst(list.get(0).get());

        for (Future<List<User>> fut : list) {
            List<User> users = fut.get();
        }

        executor.shutdown();
    }

    private static void printFirst(List<User>users){
        System.out.format("\n\n فقط درخواست یدونه از یوزرهارو چاپ کن\n");
        for(User user: users){
            System.out.println(user);
        }
    }

    static class UserCallable implements Callable<List<User>> {

        private DataSource dataSource;

        public UserCallable(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public List<User> call() throws Exception {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rset = null;
            List<User> users = new ArrayList<User>();

            try {
                // یه کانکشن بگیر
                conn = dataSource.getConnection();
                int num = ((BasicDataSource)dataSource).getNumActive();

                System.out.format("Number of Active Connections are: %d -AND- Connection info: %s & \n", num,conn.toString());

                // برای بهتر نمایش داده شدن شبیه سازی
                Thread.currentThread().sleep(2000);

                //یه استیتمنت باز کن
                stmt = conn.createStatement();

                //این کوئری رو اجرا کن
                rset = stmt.executeQuery("SELECT * FROM user");

                while (rset.next()) {
                    String firstName = rset.getString("first_name");
                    String lastName = rset.getString("last_name");
                    String username = rset.getString("username");
                    String emailAddress = rset.getString("email_address");
                    User user = new User(firstName, lastName, username, emailAddress);
                    users.add(user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rset != null) rset.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (Exception e) {
                }
            }
            return users;
        }
    }
}
