name := "s3-site-uploader"

version := "1.0"

scalaVersion := "2.11.3"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "com.github.seratch" %% "awscala" % "0.2.+"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "com.github.kxbmap" %% "configs" % "0.2.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies +=
    "org.scalamock" %% "scalamock-scalatest-support" % "3.1.4" % "test"

