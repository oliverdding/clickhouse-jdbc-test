package com.github.oliverdding.spark.test

import org.apache.spark.sql.SparkSession

trait SparkContextSetup {

  val appName: String = this.getClass.getSimpleName

  def withSparkContext(job: SparkSession => Unit): Unit = {
    lazy val spark: SparkSession = {
      SparkSession.builder.appName(appName).getOrCreate()
    }
    try {
      val start = System.currentTimeMillis()
      job(spark)
      val end = System.currentTimeMillis()
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        sys.exit(1)
    } finally {
      spark.sparkContext.stop()
    }
  }
}
