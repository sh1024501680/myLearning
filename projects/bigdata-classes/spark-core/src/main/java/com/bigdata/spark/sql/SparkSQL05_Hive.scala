
package com.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql._

object SparkSQL05_Hive {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "Administrators")

    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().enableHiveSupport().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()

    spark.sql(
      """
        | create table if not exists atguigu.user_visit_action(
        |   date                string,
        |   user_id             bigint,
        |   session_id          string,
        |   page_id             bigint,
        |   action_time         string,
        |   search_keyword      string,
        |   click_category_id   bigint,
        |    click_product_id   bigint,
        |   order_category_ids  string,
        |   order_product_ids   string,
        |   pay_category_ids    string,
        |   pay_product_ids     string,
        |   city_id             bigint)
        |row format delimited fields terminated by '\t';
        |""".stripMargin)
    spark.sql(
      """
        | load data local inpath 'datas/user_visit_action.txt' overwrite into table
        | atguigu.user_visit_action;
        |""".stripMargin)

    spark.sql(
      """
        |CREATE TABLE if not exists atguigu.product_info(
        | product_id bigint,
        | product_name string,
        | extend_info string)
        |row format delimited fields terminated by '\t';
      """.stripMargin)

    spark.sql(
      """
        | load data local inpath 'datas/product_info.txt' overwrite into table atguigu.product_info;
        |""".stripMargin)

    spark.sql(
      """
        | CREATE TABLE if not exists atguigu.city_info(
        | city_id bigint,
        | city_name string,
        | area string)
        |row format delimited fields terminated by '\t';
        |""".stripMargin)

    spark.sql(
      """
        |load data local inpath 'datas/city_info.txt' overwrite into table atguigu.city_info;
        |""".stripMargin)
    spark.close()
  }
}