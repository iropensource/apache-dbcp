package com.iropensource.dbcp;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class DataSourceSingleton {

    // این بخش رو پر کنین اطلاعات اتصال به دیتابیس می باشد
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final String DB_CONNECTION_URL = "jdbc:mysql://localhost:3306/apache_dbpc";
    private static final String DB_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final int MIN_POOL_IDLE = 3;
    private static final int MAX_POOL_IDLE = 10;
    private static final int INITIAL_POOL_SIZE = 0;
    private static final int MAX_TOTAL_POOL_SIZE = 30;
    private static final int MAX_WAIT_MILIS = 15000;
    private static volatile Object lock = new Object();
    private static volatile DataSource dataSource;


    private DataSourceSingleton(){}



    public static DataSource getInstance() {

        if(dataSource == null){
            synchronized (lock){
                if(dataSource == null){
                    BasicDataSource ds = new BasicDataSource();
                    //یوزر دیتابیس
                    ds.setUsername(USERNAME);
                    // پسورد یوزر دیتابیس
                    ds.setPassword(PASSWORD);
                    // کلاس درایوره دیتابیس
                    ds.setDriverClassName(DB_DRIVER_CLASS_NAME);
                    // کانکشن مورد نیاز برای اتصال
                    ds.setUrl(DB_CONNECTION_URL);
                    // سایز اولیه ی پول
                    ds.setInitialSize(INITIAL_POOL_SIZE);
                    //بیشترین سایزی که پول میتواند داشته باشد
                    ds.setMaxTotal(MAX_TOTAL_POOL_SIZE);
                    // کمترین سایز پول های بیکار
                    ds.setMinIdle(MIN_POOL_IDLE);
                    // بیشترین سایز برای پول های بیکار
                    ds.setMaxIdle(MAX_POOL_IDLE);
                    // زمانی که فریمورک منتظر کانکشن درگیر می ماند تا کانکشن رو مجدد برگردونه توی پول.
                    // زمانی اتفاق می افتد که همه کانکشن ها درگیرن
                    ds.setMaxWaitMillis(MAX_WAIT_MILIS);

                    //برای تنظیم مدل transaction در داخل دیتابیس
                    //ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    dataSource = ds;
                }
            }
        }

        return dataSource;
    }
}
