name := "RallyPull"

version := "0.1"

scalaVersion := "2.11.11"

//scalaVersion := "2.11.11"
javacOptions in(Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked",
  "-Xlint:deprecation", "-Xlint:-options")

scalacOptions in Compile ++= Seq("-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-Ybackend:GenBCode",
  "-Ydelambdafy:method",
  "-deprecation",
  "-unchecked",
  "-Ywarn-dead-code",
  "-feature",
  "-language:postfixOps",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  //  "-Xprint:typer",
  "-Ywarn-unused-import"
)

lazy val versions = new {
  val akkaHttp = "10.0.10"
  val akka = "2.5.6"
  val swagger = "0.7.2"
  val guice = "4.0"
  val logback = "1.2.3"
  val mockito = "1.9.5"
  val scalatest = "3.0.3"
}
//resolvers += Resolver.mavenLocal

libraryDependencies ++= {
  Seq(
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "de.heikoseeberger" %% "accessus" % "0.1.0",
    "org.scalactic" %% "scalactic" % versions.scalatest,
    "com.typesafe" % "config" % "1.3.1",
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "com.github.racc" % "typesafeconfig-guice" % "0.0.3",
    "com.typesafe.akka" %% "akka-actor" % versions.akka,
    "com.typesafe.akka" %% "akka-slf4j" % versions.akka,
    "commons-codec" % "commons-codec" % "1.10",
    "biz.paluch.redis" % "lettuce" % "4.4.0.Final",
    "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.10.0",
    "com.typesafe.akka" %% "akka-http" % versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % versions.akkaHttp,
    "com.typesafe.akka" %% "akka-testkit" % versions.akka % Test,
    "com.typesafe.akka" %% "akka-stream" % versions.akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % versions.akka % Test,
    "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
    "ch.qos.logback" % "logback-classic" % versions.logback % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % versions.akkaHttp % "test",
    "org.scalatest" %% "scalatest" % versions.scalatest % "test",
    "com.rallydev.rest" % "rally-rest-api" % "2.2.1"
  )
}

unmanagedResourceDirectories in Compile += { baseDirectory.value / "swagger" }

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("application.conf") => MergeStrategy.concat
  case PathList("com","google", xs@_*) => MergeStrategy.last
  case PathList("io","netty", xs@_*) => MergeStrategy.last
  case "META-INF/io.netty.versions.properties" => MergeStrategy.concat
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
test in assembly := {}
mainClass in assembly := Some("com.comcast.rally.pec.RallyHttpServer")
lazy val rallypull = (project in file(".")).
  enablePlugins(GitPlugin, GitBranchPrompt, GitVersioning, BuildInfoPlugin).
  settings(buildInfoKeys:=Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, git.formattedShaVersion), buildInfoPackage:="com.comcast.rally.pec")

git.useGitDescribe := true
buildInfoOptions+= BuildInfoOption.BuildTime
buildInfoOptions+= BuildInfoOption.ToJson