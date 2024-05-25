import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, explode}

object ExtractArticlesData {
  def extractArticlesData(spark: SparkSession, inputData: DataFrame): DataFrame = {
    import spark.implicits._
    val extractedArticlesData = inputData.select(
        explode($"articles").as("articles"))
      .select(
        col("articles.id"),
        col("articles.title"),
        col("articles.author_Id"),
        col("articles.citation"),
        col("articles.year"),
        col("articles.publication")
      )

    extractedArticlesData
  }

}
