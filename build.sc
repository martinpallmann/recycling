import mill._, scalalib._

object core extends ScalaModule {
  def scalaVersion = "2.12.7"
  def mainClass = Some("recycling.Main")
  def ivyDeps = Agg(
    ivy"com.softwaremill.sttp::core:1.5.0",
    ivy"io.circe::circe-core:0.10.0",
    ivy"io.circe::circe-parser:0.10.0",
    ivy"org.jsoup:jsoup:1.11.3",
    ivy"org.apache.pdfbox:pdfbox:2.0.13",
    ivy"com.itextpdf:itext7-core:7.1.4"
  )
}

