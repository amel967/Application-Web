

  import org.apache.spark.sql.{DataFrame, SparkSession}

  object GroupArticlesByCitation {
    def groupArticlesByCitation(spark: SparkSession, inputData: DataFrame): DataFrame = {
      // Extraire l'année à partir de la colonne "articles.year"
      val extractedData = inputData.select("articles.title", "articles.citation")

      // Enregistrer le DataFrame extrait en tant que vue temporaire
      extractedData.createOrReplaceTempView("articles")

      // Exécuter la requête SQL pour regrouper les titres par année
      val groupArticlesByCitation = spark.sql("SELECT citation   , title    FROM articles  ORDER BY citation DESC")

      groupArticlesByCitation
    }



}
