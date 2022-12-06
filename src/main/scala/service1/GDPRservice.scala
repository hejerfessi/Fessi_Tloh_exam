package service1

import config.Client
import config.Config.{Config, argParser}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.sql.{Dataset, SparkSession}
import scopt.OParser

import java.security.MessageDigest
import scala.sys.exit

object GDPRservice {

  /** Our main function where the action happens */


  def main(args: Array[String]) {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("Spark")
      .master("local")
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
          deleteClient(config.idClient,spark,config.filepath,config.finalpath)
        } else if(config.service == "hashData"){

          val dataset = readDataset(spark,config.filepath)
          hashData(spark,dataset,config.idClient,config.finalpath)
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
      .option("inferSchema","true")
      .csv(path)
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
      .csv(path)

//    import scala.sys.process._
//
//    s"hdfs dfs -rm /fessi_tloh_tp/bronze/data_client.csv".!
//    s"hdfs dfs -mv /fessi_tloh_tp/bronze/data_client2.csv fessi_tloh_tp/bronze/data_client.csv".!
//    dataset.show(10,false)

  }

  def deleteClient(id:Int,sparkSession: SparkSession,path:String,finalpath:String): Unit ={
    val dataset1 = readDataset(sparkSession,path)
    val dataset_filtred= delete(dataset1,id)
    dataset_filtred.show()
    writeDataset(dataset_filtred,finalpath)

  }

  def hashData(sparkSession: SparkSession,dataset: Dataset[Client],id:Long,path:String): Unit={

    import sparkSession.implicits._
    val data_filtred = dataset.filter (! col ("identifiantClient").isin(id) )
    val data_selected = dataset.filter (col ("identifiantClient").isin(id))

    val hashedColumn = udf((input: String) => MessageDigest.getInstance("SHA-256").digest(input.getBytes).map("%02x".format(_)).mkString)
    val hashedData = data_selected.withColumn("Nom", hashedColumn(col("Nom"))).withColumn("Prenom", hashedColumn(col("Prenom"))).withColumn("Adresse", hashedColumn(col("Adresse"))).as[Client]

    val finaldata=data_filtred.union(hashedData)
    finaldata.show()
    writeDataset(finaldata,path)


  }
}
