import play.PlayImport.PlayKeys._
import NativePackagerKeys._

name := "skypering"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  filters,
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "com.github.nscala-time" %% "nscala-time" % "1.8.0",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "commons-io" % "commons-io" % "2.4",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "nu.validator.htmlparser" % "htmlparser" % "1.4",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.15",
  "com.github.tototoshi" %% "play-json-naming" % "0.1.0",
  "jp.t2v" %% "play2-auth" % "0.13.0",
  "jp.t2v" %% "play2-auth-test" % "0.13.0" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test"
)

routesImport ++= Seq(
  "controllers.QueryBindable._",
  "models._"
)

includeFilter in(Assets, LessKeys.less) := "*.less"

excludeFilter in(Assets, LessKeys.less) := "_*.less"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-language:postfixOps",
  "-language:implicitConversions"
)

maintainer in Docker := "skypeline"

dockerExposedPorts in Docker := Seq(9000, 9443)

dockerBaseImage := "dockerfile/java:oracle-java8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
