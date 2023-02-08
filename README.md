# clickhouse-jdbc-test in spark

https://github.com/ClickHouse/clickhouse-java/issues/1227

## How to run

1. Install spark 2.4.5 locally and set $SPARK_HOME
2. Install JDK8, scala 2.11 and sbt
3. Build assembly jar: `sbt assembly`
4. Start clickhouse, zookeeper and kafka: `docker compose up -d`
5. Initialize clickhouse database and table in clickhouse pod

    ```sql
    create database if not exists test;
    drop table if exists test.test;
    create table if not exists test.test (
    topic String,
    offset Int64,
    value String
    ) Engine = MergeTree()
    ORDER BY (topic, offset);
    ```

6. Start spark app

    ```bash
    $SPARK_HOME/bin/spark-submit --master local \
    --deploy-mode client \
    --class com.github.oliverdding.spark.test.jobs.OfficialClickHouseJdbcLocal \
    --name run \
    ./target/scala-2.11/test.jar
    ```

7. Generate message in kafka pod

    ```bash
    kafka-topics --create --topic test --bootstrap-server localhost:9092
    kafka-console-producer --topic test --bootstrap-server localhost:9092
    ```
