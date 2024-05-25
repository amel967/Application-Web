import org.apache.spark.sql.{DataFrame, SparkSession}

object GroupTitlesByAuthors {
  def groupTitlesByAuthors(spark: SparkSession, postData: DataFrame): DataFrame = {
    postData.createOrReplaceTempView("articles")
    spark.sql(
      """
        |SELECT
        |  articles.author_Id AS author_Id,
        |  collect_list(articles.title) AS title
        |FROM
        |  articles
        |GROUP BY
        |  articles.author_Id
      """.stripMargin)
  }
}