import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, explode}

object ExtractedAuthorsData {
  def extractAuthorsData(spark: SparkSession, inputData: DataFrame): DataFrame = {
    import spark.implicits._

    val extractedAuthorsData = inputData.select(
        explode($"authors").as("authors"))
      .select(
        col("authors.id"),
        col("authors.first_name"),
        col("authors.last_name")
      )
    extractedAuthorsData
  }

}
