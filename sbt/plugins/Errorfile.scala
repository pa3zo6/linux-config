import sbt._

object Errorfile extends Plugin {

  var capturedSources: Seq[java.io.File] = Seq.empty

  def captureSources = TaskKey[Unit]("capture-sources","Capture source file names for expansion during errorfile creation")

  def expandFileNames(message: String): String = {
    val m = capturedSources map {f => Pair(f.name, f.absolutePath)}
    m.foldLeft(message){expand}
  }

  private def expand(s: String, subst: Pair[String,String]) = subst match {
    case (name, path) => s.replaceAll("\\("+name, "("+path)
  }


  def whatchErrors[T](name: String)(r: Result[T]): T = r match {
    case Inc(incomplete) => {
      new File("/tmp/sbt_errorfile~").renameTo(new File("/tmp/sbt_errorfile"))
      if ("which notify-send".!< == 0)
        Process("notify-send", List("sbt", "FAILED: " + name)) !;
      throw incomplete
    }
    case Value(value) => {
      new File("/tmp/sbt_errorfile~").renameTo(new File("/tmp/sbt_errorfile"))
      value
    }
  }
}
