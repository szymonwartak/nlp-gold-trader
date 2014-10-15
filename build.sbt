
scalaVersion := "2.10.3"

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Sonatype OSS Releases" at "https://maven.visualdna.com/nexus/content/repositories/releases",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "conjars.org" at "http://conjars.org/repo"
)

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.5",
  "org.joda" % "joda-convert" % "1.7",
  "net.databinder" %% "dispatch-http" % "0.8.10",
  "io.spray" %%  "spray-json" % "1.3.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.4.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.4.1" classifier "models"
)

