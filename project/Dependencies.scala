import sbt._
import Keys._
object Dependencies {
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.1"
  val akka_persist = "com.typesafe.akka" %% "akka-persistence" % "2.5.4"
  val leveldb = "org.iq80.leveldb" % "leveldb" % "0.7"
  val leveldbjni = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
}
