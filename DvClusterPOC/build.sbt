name := "DvClusterPOC"

version := "1.0"
scalaVersion := "2.12.4"
lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"

resolvers ++= Seq(
  "Typesafe repository"       at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots"    at "https://oss.sonatype.org/content/repositories/snapshots",
  "confluent" at "http://packages.confluent.io/maven/"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value ,
  "org.scala-lang" % "scala-compiler" % scalaVersion.value ,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion ,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion ,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion ,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion ,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion ,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion ,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion ,
  "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion ,
  "com.typesafe.akka" %% "akka-stream"          % akkaVersion ,
  "org.apache.avro" % "avro" % "1.8.2" % "compile",
  "com.sksamuel.avro4s" %% "avro4s-core" % "1.8.1",
  "io.confluent" % "kafka-streams-avro-serde" % "4.0.0",
  "org.scala-lang.modules" %% "scala-async" % "0.9.7",
  "ch.qos.logback"              %  "logback-classic"        % "1.2.3",
  "com.typesafe.scala-logging"  %% "scala-logging"          % "3.7.2"
)


libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"

exportJars := true
mainClass in Compile := Option("com.dv.akka.clusterpoc.Boot")


sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue


PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)



assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}