package galales.graphqltemplate.util

import java.nio.charset.StandardCharsets
import java.util.Base64

object codecs {
  def toBase64(s: String): String   = Base64.getEncoder.encodeToString(s.getBytes(StandardCharsets.UTF_8))
  def fromBase64(s: String): String = new String(Base64.getDecoder.decode(s))
}
