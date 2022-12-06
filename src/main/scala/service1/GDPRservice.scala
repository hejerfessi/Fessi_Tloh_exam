package service1

import config.Client
import config.Config.{Config, argParser}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.catalyst.expressions.XxHash64
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Dataset, SparkSession}
import scopt.OParser

import scala.sys.exit

object DeletePerson {

  /** Our main function where the action happens */


  def main(args: Array[String]) {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("SparkSQ")
      .getOrCreate()

    OParser.parse(argParser, args, Config()) match {
      case Some(config) =>
        println(OParser.usage(argParser))
        // do stuff with config
        if (config.service == "deleteService"){
        println(config.idClient)
        println(config.filepath)
        println(config.finalpath)

        readDataset(spark,config.filepath)
        deleteClient(config.idClient,spark,config.filepath)
        }else {


        }
      case _ =>
        exit(1)
    }

    spark.stop()
  }

  // Load each line of the source data into an Dataset
  def readDataset(sparkSession: SparkSession ,path:String):Dataset[Client]= {
    import sparkSession.implicits._
    val df = sparkSession.read
      .option("header", "true")
      .option("delimiter", ";")
      .csv(path)
      .coalesce(1)
      .as[Client]

    df

  }

  def delete(dataset: Dataset[Client],id:Long):Dataset[Client] ={
    val data_filtred = dataset.filter (! col ("identifiantClient").isin(id) )
    data_filtred.show()
    data_filtred
  }

  def writeDataset(dataset: Dataset[Client],path:String): Unit ={
    dataset.write
      .option("header", true)
      .option("delimiter",";")
      .mode("overwrite")
      .csv("/out")

//    import scala.sys.process._
//
//    s"hdfs dfs -rm /fessi_tloh_tp/bronze/data_client.csv".!
//    s"hdfs dfs -mv /fessi_tloh_tp/bronze/data_client2.csv fessi_tloh_tp/bronze/data_client.csv".!
//    dataset.show(10,false)

  }

  def deleteClient(id:Int,sparkSession: SparkSession,path:String): Unit ={
    val dataset1 = readDataset(sparkSession,path)
    val dataset_filtred= delete(dataset1,id)
    dataset_filtred.show()
    writeDataset(dataset_filtred,path)

  }

  def hash(s:String): String ={
    val hash = XxHash64.hashCode(s)
  }

  def hashData(sparkSession: SparkSession,dataset: Dataset[Client],id:Int): Unit ={

    val data_filtred = dataset.filter (! col ("identifiantClient").isin(id) )
    val data_filtred2 = dataset.filter (col ("identifiantClient").isin(id) ).map(client => (client.identifiantClient,client.nom.))




  }
}
