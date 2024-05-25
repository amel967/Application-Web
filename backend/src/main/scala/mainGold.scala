import GroupArticlesByCitation.groupArticlesByCitation
import GroupTitlesByAuthors.groupTitlesByAuthors
import GroupTitlesByYear.groupTitlesByYear
import org.apache.spark.sql.{DataFrame, SparkSession}

object mainGold {

  def main(args: Array[String]): Unit = {
    // Création d'une session Spark
    val spark = SparkSession.builder()
      .appName("Extract Articles Data")
      .master("local[*]") // Utilisation du mode local pour les tests
      .getOrCreate()

    // Read your input data into a DataFrame (replace the path with your actual data source)
    val inputData: DataFrame = spark.read.option("multiline", "true").json("data.json")

    // Appel de la fonction pour regrouper les titres par année
    val groupDataByYear = groupTitlesByYear(spark, inputData)
    val groupDataByAuthors = groupTitlesByAuthors(spark, inputData)
    val groupDataByCitation = groupArticlesByCitation(spark, inputData)

    // Affichage des données regroupées
    groupDataByYear.show()
    groupDataByAuthors.show()
    groupDataByCitation.show()

    // Arrêt de la session Spark
    spark.stop()
  }

}
