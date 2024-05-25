import ExtractArticlesData.extractArticlesData
import ExtractedAuthorsData.extractAuthorsData
import org.apache.spark.sql.{DataFrame, SparkSession}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.collection.mutable

object api {

  // Case classes for JSON marshalling
  case class Author(id: Long, first_name: String, last_name: String)
  case class Article(id: Long, title: String, year: String ,publication : String , author_Id: Seq[Long])

  case class ArticleByCitation(citation: String, titles: Seq[String] ,year:Seq[String])

  // JSON format for case classes
  implicit val authorFormat = jsonFormat3(Author)
  implicit val articleFormat = jsonFormat5(Article)


  implicit val articleByCitationFormat = jsonFormat3(ArticleByCitation)

  def main(args: Array[String]): Unit = {
    // Initialize Spark
    val spark = SparkSession.builder()
      .appName("SparkRESTExample")
      .master("local[*]")
      .config("spark.driver.host", "localhost")
      .getOrCreate()


    val inputData: DataFrame = spark.read.option("multiline", "true").json("data.json")

    // Register DataFrames as temporary views
    val extractedArticlesData = extractArticlesData(spark, inputData)
    extractedArticlesData.createOrReplaceTempView("articles")

    val extractedAuthorsData = extractAuthorsData(spark, inputData)
    extractedAuthorsData.createOrReplaceTempView("authors")

    // CORS settings
    val corsHeaders = List(
      `Access-Control-Allow-Origin`.*,
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With"),
      `Access-Control-Allow-Methods`(OPTIONS, GET, POST, PUT, DELETE)
    )

    def corsHandler(r: Route): Route = respondWithHeaders(corsHeaders) {
      options {
        complete(HttpResponse(StatusCodes.OK).withHeaders(corsHeaders))
      } ~ r
    }

    // Define the routes
    val routes: Route = corsHandler {
      path("authors") {
        get {
          complete {
            val result = spark.sql("SELECT id, first_name, last_name FROM authors ORDER BY id ASC")
            result.collect().map(row => Author(row.getAs[Long]("id"), row.getAs[String]("first_name"), row.getAs[String]("last_name"))).toJson
          }
        }
      } ~
        path("articles") {
          get {
            complete {
              val result = spark.sql("SELECT id, title, year,publication ,author_Id FROM articles ORDER BY id ASC")
              result.collect().map(row =>
                Article(
                  row.getAs[Long]("id"),
                  row.getAs[String]("title"),
                  row.getAs[String]("year"),
                  row.getAs[String]("publication"),
                  row.getAs[mutable.WrappedArray[Long]]("author_Id").toList // Convert to List
                )
              ).toJson            }
          }
        } ~
        path("articlesByCitation") {
          get {
            complete {
              val resultByCitation = spark.sql("SELECT citation, coll" +
                "ect_list(title) AS titles  , collect_list(year) AS years FROM articles GROUP BY citation ORDER BY citation DESC")
              val jsonResultByCitation = resultByCitation.collect().map(row => ArticleByCitation(row.getAs[String]("citation"), row.getSeq[String](1),row.getSeq[String](2))).toJson
              JsObject("byCitation" -> jsonResultByCitation)
            }
          }
        }
    }

    // Start Akka HTTP server
    implicit val system: ActorSystem = ActorSystem("rest-api")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

    println(s"Server online at http://localhost:8080/")
  }
}