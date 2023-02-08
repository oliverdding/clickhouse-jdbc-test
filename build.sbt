val sparkVersion = sys.props.getOrElse("SPARK_VERSION", "2.4.5")

ThisBuild / scalaVersion     := "2.11.12"

lazy val root = (project in file("."))
  .settings(
    name := "spark-test",
    idePackagePrefix := Some("com.github.oliverdding.spark.test"),
    assembly / assemblyJarName := "test.jar",
    libraryDependencies ++= Seq(
      // Spark
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
      "com.clickhouse" % "clickhouse-jdbc" % "0.4.0" exclude("*", "*") classifier "all"
    )
  )

assembly / assemblyMergeStrategy := {
  case "reference.conf" => MergeStrategy.concat
  case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
