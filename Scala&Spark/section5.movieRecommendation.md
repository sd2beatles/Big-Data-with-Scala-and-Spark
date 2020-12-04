

```scala



import org.apache.log4j._
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType,LongType,StructType,StringType}
import org.apache.spark.sql.{Dataset,DataFrame}
object ratingCounters {

  case class Movies(userID: Int, movieID: Int, rating: Int, timestamp: Long)

  case class MoviesNames(movieID: Int, movieTitle: String)

  case class MoviePairs(movie1: Int, movie2: Int, rating1: Int, rating2: Int)

  case class MovieSimilarity(movie1: Int, movie2: Int, score: Double, numPairs: Long)

  def computeCosineSimilarity(spark: SparkSession, data: Dataset[MoviePairs]): Dataset[MovieSimilarity] = {
    val pairscore = data
      .withColumn("xx", col("rating1") * col("rating1"))
      .withColumn("yy", col("rating2") * col("rating2"))
      .withColumn("xy", col("rating1") * col("rating2"))

    val calculateSimilarity = pairscore
      .groupBy("movie1", "movie2")
      .agg(
        sum(col("xy")).alias("numerator"),
        (sqrt(sum(col("xx"))) * sqrt(sum(col("yy")))).alias("denominator"),
        count(col("xy")).alias("numPairs")
      )

    import spark.implicits._
    val result = calculateSimilarity
      .withColumn("score",
        when(col("denominator") =!= 0, col("numerator") / col("denominator")).otherwise(null))
      .select("movie1", "movie2", "score", "numPairs").as[MovieSimilarity]
    result
  }

  def getMovieName(movieNames: Dataset[MoviesNames], movieID: Int): String = {
    val result = movieNames.filter(col("movieID") === movieID).select("movieTitle").collect()(0)
    result(0).toString
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession
      .builder
      .appName("MovieSimilairities")
      .master("local[*]")
      .getOrCreate()
    val movieNameSchema = new StructType()
      .add("movieID", IntegerType, nullable = true)
      .add("movieTitle", StringType, nullable = true)

    val movieSchema = new StructType()
      .add("userID", IntegerType, nullable = true)
      .add("movieID", IntegerType, nullable = true)
      .add("rating", IntegerType, nullable = true)
      .add("timestamp", LongType, nullable = true)

    println("\nLoading movie names..")
    import spark.implicits._
    val movieNames = spark.read
      .option("sep", "|")
      .option("charset", "ISO-8859-1")
      .schema(movieNameSchema)
      .csv("C:/SparkScalaCourse/SparkScalaCourse/data/ml-100k/ml-100k/u.item")
      .as[MoviesNames]

    val movies = spark.read
      .option("sep", "\t")
      .schema(movieSchema)
      .csv("C:/SparkScalaCourse/SparkScalaCourse/data/ml-100k/ml-100k/u.data")
      .as[Movies]

    val ratings = movies
      .select("userID", "movieID", "rating")

    val moviePairs = ratings.as("ratings1")
      .join(ratings.as("ratings2"), $"ratings1.userID" === $"ratings2.userID" && $"ratings1.movieID" < $"ratings2.movieID")
      .select($"ratings1.movieID".as("movie1"),
        $"ratings2.movieID".as("movie2"),
        $"ratings1.rating".as("rating1"),
        $"ratings2.rating".as("rating2")
      ).as[MoviePairs]


    //store the infomration on pair similarities of each pair in the cache for the further use
    val moviePairSimiliarities = computeCosineSimilarity(spark, moviePairs).cache()
    moviePairSimiliarities.show(10)
    if (args.length > 0) {
      val scoreThreshold = 0.97
      val conCurrentThreshold = 50.0
      val movieID = args(0).toInt
      val filteredResult = moviePairSimiliarities.filter(
        (col("movie1") === movieID || col("movie2") === movieID) &&
          col("score") > scoreThreshold && col("numPairs") > conCurrentThreshold)
      val results = filteredResult.sort(col("score").desc).take(10)
      println("\nTop 10 similar movies for " + getMovieName(movieNames, movieID))
    for(result<-results){
      var similarMovieID=result.movie1
      if(similarMovieID==movieID){
        similarMovieID=result.movie2
      }
      println(getMovieName(movieNames,similarMovieID)+"\tscore:"+result.score+"\tstrength:"+result.numPairs)
    }
    }
  }
}



```
