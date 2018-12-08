package tagsoup

import org.jsoup.nodes.Element
import scala.collection.JavaConverters._

class Elem(el: Element) {

  def ~(s: String): List[Elem] =
    el.select(s)
      .iterator()
      .asScala
      .toList
      .map(x => new Elem(x))

  def text: String = el.text()

  def attr(s: String): Option[String] = {
    val res = el.attr(s)
    if (res.isEmpty) {
      None
    } else {
      Some(res)
    }
  }

  override def toString: String = el.toString
}
