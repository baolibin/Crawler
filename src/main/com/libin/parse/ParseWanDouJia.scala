package libin.parse

import libin.utils.HtmlUtils
import org.htmlcleaner.{TagNode, HtmlCleaner}

import scala.StringBuilder

/**
  * Created by baolibin on 2017/10/15.
  */
object ParseWanDouJia {
  /**
    * 解析豌豆荚页面
    */
  def parseWanDouJia(content: String): String = {
    val sb: StringBuilder = new StringBuilder
    val htmlCleaner: HtmlCleaner = new HtmlCleaner
    val rootNode: TagNode = htmlCleaner.clean(content)
    val level1CategoryName = HtmlUtils.getText(rootNode, "/html/body/div[3]/div[1]/div[1]/a/span")
    println("一级标题:" + level1CategoryName)
    val level2CategoryName = HtmlUtils.getText(rootNode, "/html/body/div[3]/div[1]/div[2]/a/span")
    println("二级标题:" + level2CategoryName)
    sb.toString()
  }
}
