name := "game-of-life"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.7"
libraryDependencies += "org.scalaz" %% "scalaz-concurrent" % "7.2.7"

coverageEnabled in Test := true
