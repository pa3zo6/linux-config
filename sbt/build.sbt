extraLoggers := {
  val logger = new Logger {
    def trace(t: => Throwable): Unit = {}
    def success(message: => String): Unit = {}
    def log(level: Level.Value, message: => String): Unit = {
      if (message.endsWith("[0m") || level == Level.Error) {
        val fw = new java.io.FileWriter("/tmp/sbt_errorfile~", true)
        fw.write(expandFileNames(message)+"\n")
        fw.close()
      }
    }
  }
  (_) => Seq(new FullLogger(logger))
}

captureSources <<= (sources in Compile, sources in Test) map { (c,t) => capturedSources = c ++ t }

compile in Compile <<= (compile in Compile) mapR whatchErrors("cc")

compile in Test <<= (compile in Test) mapR whatchErrors("ct")

test in Test <<= ((test in Test) mapR whatchErrors("tt")) dependsOn captureSources

testOnly in Test <<= ((testOnly in Test) mapR whatchErrors("to")) dependsOn captureSources

