package libin.utils

import org.htmlcleaner.{XPatherException, TagNode}

/**
  * Created by baolibin on 2017/10/15.
  * 解析一个HTML页面的工具类
  */
object HtmlUtils {
  def getText(rootNode: TagNode, xpath: String): String = {
    var result: String = null
    try {
      val evaluateXPath: Array[AnyRef] = rootNode.evaluateXPath(xpath)
      if (evaluateXPath.length > 0) {
        val tagNode: TagNode = evaluateXPath(0).asInstanceOf[TagNode]
        result = tagNode.getText.toString
      }
    } catch {
      case e: XPatherException => result = "XPatherException"
    }
    result
  }
}
