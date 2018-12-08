package tagsoup

import java.io.ByteArrayInputStream
import com.itextpdf.kernel.pdf.{PdfDocument, PdfReader}
import org.jsoup.Connection

class Response(wrapped: Connection.Response) {

  def html: Doc =
    new Doc(wrapped.parse())

  def pdf: PdfDocument =
    new PdfDocument(new PdfReader(new ByteArrayInputStream(wrapped.bodyAsBytes())))
}
