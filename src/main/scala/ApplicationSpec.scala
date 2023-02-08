package com.github.oliverdding.spark.test

import org.apache.spark.sql.SparkSession

abstract class ApplicationSpec extends Object with SparkContextSetup {

  def main(args: Array[String]): Unit = {
    withSparkContext(spark => {
      run(spark)
    })
  }

  def run(spark: SparkSession): Unit
}
