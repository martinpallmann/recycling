package tagsoup

import org.jsoup.Connection.Method
import org.jsoup.Jsoup

object Tagsoup {

  def get(url: String) =
    new Response(Jsoup.connect(url).method(Method.GET).ignoreContentType(true).execute())
}
