package libin.test

import java.io.IOException
import java.util.regex.Pattern

import libin.download.DownloadInfo
import libin.utils.{HtmlUtils, PageUtils}
import org.apache.commons.lang3.StringUtils
import org.htmlcleaner.{HtmlCleaner, TagNode}

import scala.collection.mutable

/**
  * Created by baolibin on 17-10-17.
  */
object test {
  def main(args: Array[String]): Unit = {
    /*val doc: Document = Jsoup.parse(wanDouJiaPage)
    val contents: Elements = doc.select("div.tag-box")
    println(contents.size())
    val it = contents.iterator()
    while (it.hasNext) {
      val element: Element = it.next
      val titleNode: Node = element.child(0).childNode(0)
      println(titleNode.attr("a"))
    }*/
    /*//用正则获取标签里的内容,现在还是在用xPath进行解析
    val set = new mutable.HashSet[String]()
    val linkRegex =
      """ <a href="http://www.wandoujia.com/tag/([0-9]*)">(\D*)</a> """.trim.r
    for (m <- linkRegex.findAllIn(PageUtils.httpUrlSpider(url)).matchData) {
      set += m.group(2).trim
    }
    println(set.mkString("|"))*/

    /*val content: String = DownloadInfo.downloadWanDouJia("http://www.wandoujia.com/category/5029_716/1")
    var pageNumber = 0
    val linkRegex =
      """ <a class="page-item " href="http://www.wandoujia.com/category/([a-zA-Z0-9/_]*)">([0-9]*)</a>(\D*)<a class="page-item next-page " href="http://www.wandoujia.com/category/([a-zA-Z0-9/_]*)">(\D*)</a> """.trim.r

    for (m <- linkRegex.findAllIn(content).matchData) {
      pageNumber = m.group(2).trim.toInt
    }
    println(pageNumber)*/


    var updateInfo = ""
    var updateContent = ""
    try {
      val strContent: String = PageUtils.httpUrlSpider("http://www.wandoujia.com/apps/com.folk.ringtone")
      println(strContent)
      if (StringUtils.isNotBlank(strContent)) {
        val pat1 = "([\\s\\S]*)itemprop=\"description\">([\\s\\S]*)</div>([\\r\\n\\s]*)<a style=\"display([\\s\\S]*)"
        val pat2 = "([\\s\\S]*)<div data-originheight=\"100\" class=\"con\">([\\s\\S]*)</div>([\\s\\S]*)"
        val compile1 = Pattern.compile(pat1)
        val matcher1 = compile1.matcher(strContent)
        if (matcher1.find) {
          updateInfo = matcher1.group(2).trim.split("</div> ")(0).replaceAll("</br>", "").replaceAll("<br>", "")
          println("应用信息："+ updateInfo)
        }else System.out.println("应用信息未匹配到!")

        if(strContent.contains("<div data-originheight=\"100\" class=\"con\">")){
          val compile2 = Pattern.compile(pat2)
          val matcher2 = compile2.matcher(strContent)
          if (matcher2.find) {
            updateContent = matcher2.group(2).trim.split("</div> ")(0).replaceAll("</br>", "").replaceAll("<br>", "")
            println("更新内容："+ updateContent)
          }else System.out.println("更新内容未匹配到!")
        }else System.out.println("更新内容未匹配到!")
      }
      else System.out.println("HttpURLConnection下载页面为空!")
    } catch {
      case e: IOException =>
        println("下载应用信息和更新内容时Connection reset!")
        updateInfo = "Connection reset"
        updateContent = "Connection reset"
    }



    //parseWanDouJiaSoftware(PageUtils.getHttpClientContent("http://www.wandoujia.com/category/app"))
    //parseWanDouJiaSoftware(PageUtils.getHttpClientContent("http://www.wandoujia.com/category/game"))
  }

  /**
    * 下载豌豆荚的第一步
    * 解析所有三级分类的URL
    * 初始URL地址：http://www.wandoujia.com/category/app
    */
  def parseWanDouJiaSoftware(content: String): mutable.HashMap[String, String] = {
    val filterList = List("影音播放", "系统工具", "通讯社交", "手机美化", "新闻阅读", "摄影图像", "考试学习", "网上购物",
      "金融理财", "生活休闲", "旅游出行", "健康运动", "办公商务", "育儿亲子")
    val htmlCleaner: HtmlCleaner = new HtmlCleaner
    val rootNode: TagNode = htmlCleaner.clean(content)
    val rootPath = new mutable.HashMap[String, String]()

    val linkRegex =
      """ <li class="child-cate"><a href="([a-zA-Z0-9:/._]*)" title="(\D*)">(\D*)</a></li> """.trim.r

    //获取软件有效的分类名
    val androidSoftware = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/ul[1]").trim.split("\n")
      .filter(line => StringUtils.isNoneBlank(line.trim) && !filterList.contains(line.trim)).map(line => line.trim).toList

    //获取有效的分类名和URL地址
    for (m <- linkRegex.findAllIn(content).matchData) {
      if (androidSoftware.contains(m.group(3))) {
        rootPath += m.group(3).trim -> m.group(1).trim
      }
    }
    println(rootPath.mkString("\n"))
    println("软件应用三级url一共:" + rootPath.size)
    rootPath
  }


  /**
    * 下载豌豆荚的第一步
    * 解析所有三级分类的URL
    * 初始URL地址：http://www.wandoujia.com/category/game
    */
  def parseWanDouJiaGame(content: String): mutable.HashMap[String, String] = {
    val filterList = List("休闲益智", "跑酷竞速", "扑克棋牌", "动作冒险", "飞行射击", "经营策略", "网络游戏", "体育竞技",
      "角色扮演", "辅助工具")
    val htmlCleaner: HtmlCleaner = new HtmlCleaner
    val rootNode: TagNode = htmlCleaner.clean(content)
    val rootPath = new mutable.HashMap[String, String]()

    val linkRegex =
      """ <li class="child-cate"><a href="([a-zA-Z0-9:/._]*)" title="(\D*)">(\D*)</a></li> """.trim.r

    //获取软件有效的分类名
    val androidSoftware = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/ul[1]").trim.split("\n")
      .filter(line => StringUtils.isNoneBlank(line.trim) && !filterList.contains(line.trim)).map(line => line.trim).toList

    //获取有效的分类名和URL地址
    for (m <- linkRegex.findAllIn(content).matchData) {
      if (androidSoftware.contains(m.group(3))) {
        rootPath += m.group(3).trim -> m.group(1).trim
      }
    }
    println(rootPath.mkString("\n"))
    println("游戏应用三级url一共:" + rootPath.size)
    rootPath
  }
}
