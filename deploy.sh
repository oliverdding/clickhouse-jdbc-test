#!/bin/bash

kubectl delete pvc --ignore-not-found=true spark-data
kubectl delete sparkapp --ignore-not-found=true spark-test-oliverdding

CLIENT_POD=$(kubectl -n default get pods | grep client | head -1 | awk '{print $1}')
kubectl -n default cp ./target/scala-2.11/test.jar "$CLIENT_POD":/tmp/spark-clickhouse-connector-jar-by-oliverdding
kubectl -n default exec "$CLIENT_POD" -- hdfs --config /etc/hadoop-custom-conf/ dfs -rm -f hdfs://kdl-hadoop-hdfs-nn/tmp/oliverdding/test.jar
kubectl -n default exec "$CLIENT_POD" -- hdfs --config /etc/hadoop-custom-conf/ dfs -put /tmp/spark-clickhouse-connector-jar-by-oliverdding hdfs://kdl-hadoop-hdfs-nn/tmp/oliverdding/test.jar

kubectl apply -f spark-test-oliverdding.yaml
