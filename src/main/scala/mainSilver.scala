
import org.apache.spark.sql.{DataFrame, SparkSession}

object mainSilver {
  def main(args: Array[String]): Unit = {
    // Création d'une session Spark
    val spark = SparkSession.builder()
      .appName("Extract Articles Data")
      .master("local[*]")
      .getOrCreate()


    val inputData: DataFrame = spark.read.option("multiline", "true").json("data.json")
    inputData.show()

    val articlesData: DataFrame = ExtractArticlesData.extractArticlesData(spark, inputData)
    articlesData.show()

    val authorsData: DataFrame = ExtractedAuthorsData.extractAuthorsData(spark, inputData)
    authorsData.show()

    spark.stop()
  }

}
