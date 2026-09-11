package com.universidad.sparkdb.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Value("${spark.app.name:SparkSpringBootDB}")
    private String appName;

    @Value("${spark.master:local[*]}")
    private String master;

    @Bean
    public SparkSession sparkSession() {
        // Fix para Windows: apunta hadoop.home.dir al raíz del proyecto donde
        // existe bin/winutils.exe (descargado de cdarlint/winutils para Hadoop 3.3)
        String userDir = System.getProperty("user.dir");
        System.setProperty("hadoop.home.dir", userDir);

        SparkConf conf = new SparkConf()
                .setAppName(appName)
                .setMaster(master)
                .set("spark.driver.host", "localhost")
                .set("spark.ui.enabled", "false")
                .set("spark.sql.adaptive.enabled", "true")
                .set("spark.driver.extraJavaOptions",
                        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED " +
                        "--add-opens=java.base/java.nio=ALL-UNNAMED " +
                        "-Dio.netty.tryReflectionSetAccessible=true");

        return SparkSession.builder()
                .config(conf)
                .getOrCreate();
    }
}
