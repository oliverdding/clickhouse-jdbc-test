package com.github.oliverdding.spark.test
package jobs

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.Properties

object OfficialClickHouseJdbcLocal extends ApplicationSpec {

  val jdbcUrl: String = "jdbc:clickhouse://127.0.0.1:8123/test"
  val kafkaBootstrapServers: String = "127.0.0.1:29092"

  import org.apache.spark.sql.functions.udf

  private val binaryToString = udf((payload: Array[Byte]) => new String(payload))

  override def run(spark: SparkSession): Unit = {

    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", "test")
      .option("failOnDataLoss", "false")
      .option("maxOffsetsPerTrigger", 10L)
      .load()

    val writer = (batchDF: DataFrame, batchId: Long) =>
      if (!batchDF.isEmpty) {

        val connectionProperties = new Properties()
        connectionProperties.put("user", "default")
        connectionProperties.put("password", "")
        connectionProperties.put("ssl", "false")

        batchDF
          .coalesce(1)
          .write
          .mode("append")
          .option("driver", "com.clickhouse.jdbc.ClickHouseDriver")
          .option("numPartitions", "1")
          .option("batchsize", "10")
          .option("isolationLevel", "NONE")
          .jdbc(jdbcUrl, "test.test", connectionProperties)
      }

    val query = df
      .withColumn("value", binaryToString(col("value")))
      .select("topic", "offset", "value")
      .writeStream
      .outputMode("append")
      .queryName(s"test.clickHouse")
      .foreachBatch(writer)
      .trigger(Trigger.ProcessingTime(s"5 seconds"))
      .option(
        "checkpointLocation",
        "file:///tmp/checkpoint/"
      )
      .start()

    query.awaitTermination()
  }
}
