//package com.spark.recommendation
//
//import org.apache.spark.mllib.recommendation.ALS
//import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
//import org.apache.spark.mllib.recommendation.Rating
//import org.apache.spark.{SparkConf, SparkContext}
//import com.spark.recommendation.FeatureExtraction
//
//object MatrixFactorization {
//  def MF(): Unit = {
////    val sc = new SparkContext(new SparkConf().setMaster("local").setAppName("MF"))
//     val ratings = FeatureExtraction.getFeatures()
//     val rank = 10
//     val numIterations = 10
//     val model =ALS.train(ratings, rank, numIterations, 0.01)
//
//     val userProduct = ratings.map{case Rating(user, product, rate) => (user, product)}
//
//}

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.mllib.recommendation.Rating

import scala.collection.mutable.ListBuffer

/**
  * A simple Spark app in Scala
  */
object MovieLensFPGrowthApp {

  val PATH = "C:/Users/logan/Desktop"
  def main(args: Array[String]) {

    //val file = new File("output-" + new Util().getDate() + ".log")
    //val bw = new BufferedWriter(new FileWriter(file))
    val conf = new SparkConf()
    conf.set("spark.app.name", "my Spark app")
      .set("spark.master", "local[4]")
      .set("spark.ui.port", "36000")  //键值对
    val sc = new SparkContext(conf)
//    val sc = new SparkContext("local[2]", "Chapter 5 App")
    val rawData = sc.textFile(PATH + "/ml-100k/u.data")
    rawData.first()
    // 14/03/30 13:21:25 INFO SparkContext: Job finished: first at <console>:17, took 0.002843 s
    // res24: String = 196	242 	3	881250949

    /* Extract the user id, movie id and rating only from the dataset */
    val rawRatings = rawData.map(_.split("\t").take(3))
    rawRatings.first()
    // 14/03/30 13:22:44 INFO SparkContext: Job finished: first at <console>:21, took 0.003703 s
    // res25: Array[String] = Array(196, 242, 3)

    val ratings = rawRatings.map { case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble) }
    val ratingsFirst = ratings.first()
    println(ratingsFirst)

    val userId = 789
    val K = 10

    val movies = sc.textFile(PATH + "/ml-100k/u.item")
    val titles = movies.map(line => line.split("\\|").take(2)).map(array => (array(0).toInt, array(1))).collectAsMap()
    titles(123)

    var eRDD = sc.emptyRDD
    var z = Seq[String]()

    val l = ListBuffer()
    val aj = new Array[String](100)
    var i = 0
    for( a <- 801 to 900) {
      val moviesForUserX = ratings.keyBy(_.user).lookup(a)
      val moviesForUserX_10 = moviesForUserX.sortBy(-_.rating).take(10)
      val moviesForUserX_10_1 = moviesForUserX_10.map(r => r.product)
      var temp = ""
      for( x <- moviesForUserX_10_1){
        temp = temp + " " + x
        println(temp)

      }

      aj(i) = temp
      i += 1
    }
    z = aj
    val transaction2 = z.map(_.split(" "))

    val rddx = sc.parallelize(transaction2, 2).cache()

    val fpg = new FPGrowth()
    val model6 = fpg
      .setMinSupport(0.1)
      .setNumPartitions(1)
      .run(rddx)

    model6.freqItemsets.collect().foreach { itemset =>
      println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)
    }
    sc.stop()
  }

}

