name := "game-of-life"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.7"
libraryDependencies += "org.scalaz" %% "scalaz-concurrent" % "7.2.7"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

coverageEnabled in Test := true
