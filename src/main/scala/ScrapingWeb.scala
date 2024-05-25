import org.json4s._
import org.json4s.native.JsonMethods._
import org.jsoup.Jsoup
import scalaj.http._

import java.io.FileWriter
import java.net.URLEncoder
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent._
import scala.concurrent.duration._

object ScrapingWeb {
  implicit val formats = DefaultFormats

  case class Authors(id: Int, first_name: String, last_name: String)

  case class Articles(id: Int, title: String, author_Id: List[Int], citation: String, year: String, publication: String)

  def cleanYear(yearString: String): String = {
    // Remplacer tout ce qui n'est pas un chiffre par une chaîne vide
    yearString.replaceAll("\\D", "")
  }

  def cleanCitation(citationString: String): String = {
    // Remplacer tout ce qui n'est pas un chiffre par une chaîne vide
    citationString.replaceAll("\\D", "")
  }

  def main(args: Array[String]): Unit = {
    // Définir les URLs à récupérer
    val keywords = URLEncoder.encode("projet eau souterrain Tunisie projet eau surface Tunisie projet en ressources eau Tunisie", "UTF-8")
    val urls = (2020 to 2024).flatMap { year =>
      List(
        s"https://scholar.google.com/scholar?q=projet+%2B+eau+%2B+souterrain+%2B+Tunisie&hl=fr&as_sdt=0%2C5&as_ylo=$year&as_yhi=$year",
        s"https://scholar.google.com/scholar?q=projet+%2B+eau+%2B+surface+%2B+Tunisie&hl=fr&as_sdt=0%2C5&as_ylo=$year&as_yhi=$year",
        s"https://scholar.google.com/scholar?q=projet+%2B+ressources+%2B+eau+%2B+Tunisie&hl=fr&as_sdt=0%2C5&as_ylo=$year&as_yhi=$year"
      )
    }


    val pagesToScrape = 5

    // Créer une liste mutable pour stocker les articles
    var articlesList = mutable.ListBuffer[Articles]()
    var idCounter = 0 // Définir un compteur d'ID pour les articles

    // Créer une liste mutable pour stocker les auteurs
    var authorsList = mutable.ListBuffer[Authors]()
    var authorIdCounter = 0 // Définir un compteur d'ID pour les auteurs

    // Boucle pour chaque URL
    for (url <- urls) {
      // Boucle pour chaque année de 2020 à 2024
      for (year <- 2020 to 2024) {
        val date = s"begin=$year-01-01&end=$year-12-31" // Construire la plage de dates pour l'année spécifique

        for (pageNumber <- 0 until pagesToScrape) {
          // Construire l'URL pour la page actuelle avec la date et le numéro de page
          val currentUrl = s"$url&start=${pageNumber * 10}&$date"

          // Faire une requête HTTP vers l'URL
          val response = Http(currentUrl).asString

          // Analyser le contenu HTML
          val doc = Jsoup.parse(response.body)

          // Sélectionner tous les éléments du document HTML avec la classe 'gs_ri'
          val elements = doc.select("div.gs_ri").asScala

          // Parcourir chaque élément et extraire les données
          for (element <- elements) {
            var title = element.select("h3.gs_rt").text()
            var citation = cleanCitation(element.select("div.gs_fl > a[href^=/scholar?cites=]").text())



            // Nettoyer les mots-clés "PDF" et "html", et les symboles `[` `]` dans le titre
            title = title.replaceAll("\\bPDF\\b", "").replaceAll("\\bHTML\\b", "").replaceAll("\\[|\\]", "").trim()

            // Nettoyer les mots-clés "cite" et "fois" dans la citation
            citation = citation.replaceAll("\\bCité\\b", "").replaceAll("\\bfois\\b", "").trim()



            val authorsRaw = element.select("div.gs_a").text()

            val firstDashIndex = authorsRaw.indexOf('-')
            val authorsSplit = if (firstDashIndex != -1) authorsRaw.substring(0, firstDashIndex).split(", ") else authorsRaw.split(", ")

            // Pour chaque auteur, divisez le nom en prénom et nom de famille
            val author_Id = authorsSplit.map { author =>
              val nameParts = author.trim().split("\\s+")
              val first_name = nameParts.head
              val last_name = if (nameParts.length > 1) nameParts.tail.mkString(" ") else ""

              // Vérifiez si l'auteur existe déjà dans la liste des auteurs
              val authorIndex = authorsList.indexWhere(a => a.first_name == first_name && a.last_name == last_name)
              if (authorIndex != -1) {
                authorsList(authorIndex).id // Récupérer l'ID de l'auteur existant
              } else {
                authorIdCounter += 1
                val newAuthor = Authors(authorIdCounter, first_name, last_name)
                authorsList += newAuthor // Ajouter le nouvel auteur à la liste
                newAuthor.id
              }
            }.toList

            val publicationInfo = if (firstDashIndex != -1 && firstDashIndex + 1 < authorsRaw.length) authorsRaw.substring(firstDashIndex + 1) else ""
            val secondDashIndex = publicationInfo.lastIndexOf('-')
            val year = cleanYear(if (secondDashIndex != -1) publicationInfo.substring(0, secondDashIndex).trim().split(',').lastOption.getOrElse("") else "")
            val publication = if (secondDashIndex != -1 && secondDashIndex + 1 < publicationInfo.length) publicationInfo.substring(secondDashIndex + 1).trim() else ""

            // Incrémenter l'ID pour chaque article
            idCounter += 1

            // Créer un objet Article à partir des données extraites
            val article = Articles(idCounter, title, author_Id, citation, year, publication)

            // Ajouter l'article à la liste
            articlesList += article
          }

          // Attendre 30 secondes avant de passer à la prochaine page
          Thread.sleep(30.seconds.toMillis)
        }
      }
    }

    // Combinez les articles et les auteurs dans une seule structure de données
    val combinedData = Map("articles" -> articlesList.toList, "authors" -> authorsList.toList)

    // Convertir la structure de données en JSON
    val json = Extraction.decompose(combinedData)

    // Écrire le JSON dans un fichier
    val file = new FileWriter("data.json")
    try {
      file.write(pretty(render(json)))
    } finally {
      file.close()
    }

    println("Données enregistrées avec succès dans data.json")
  }
}
