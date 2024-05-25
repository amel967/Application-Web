
import org.apache.spark.sql.{DataFrame, SparkSession}

object GroupTitlesByYear {
  def groupTitlesByYear(spark: SparkSession, inputData: DataFrame): DataFrame = {
    // Extraire l'année à partir de la colonne "articles.year"
    val extractedData = inputData.select("articles.title", "articles.year")

    // Enregistrer le DataFrame extrait en tant que vue temporaire
    extractedData.createOrReplaceTempView("articles")

    // Exécuter la requête SQL pour regrouper les titres par année
    val groupedTitlesByYear = spark.sql("SELECT year, title  FROM articles  ORDER BY year")

    groupedTitlesByYear
  }
}
