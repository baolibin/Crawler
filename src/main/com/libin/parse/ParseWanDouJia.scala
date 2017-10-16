package libin.parse

import libin.utils.HtmlUtils
import org.htmlcleaner.{TagNode, HtmlCleaner}

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
    val level1CategoryName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[1]/div[1]/a/span")
    println("一级标题:" + level1CategoryName.trim)
    val level2CategoryName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[1]/div[2]/a/span")
    println("二级标题:" + level2CategoryName.trim)
    val appName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[2]/p[1]/span")
    println("App名字:" + appName.trim)
    val downloads = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[4]/span[1]/i")
    println("下载量:" + downloads.trim)
    val favorableRate = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[4]/span[2]/i")
    println("好评率:" + favorableRate.trim)
    val commentNumber = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[4]/div[1]/a/i")
    println("评论数:" + commentNumber.trim)
    val comment1 = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[2]/p[2]")
    println("小编或用户点评1:" + comment1.trim)
    val comment2 = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[1]/div[1]/div")
    println("小编或用户点评2:" + comment2.trim)
    val size = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[1]")
    println("大小:" + size.trim)
    val updateTime = HtmlUtils.getText(rootNode, "//*[@id=\"baidu_time\"]")
    println("更新时间:" + updateTime.trim)
    val versionName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[5]")
    println("版本:" + versionName.trim.split(";")(1))
    val compatibility = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[6]")
    println("兼容性:" + compatibility.trim.split("\n")(0))
    val publisherName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[7]/span")
    println("开发商|作者:" + publisherName.trim)
    sb.toString()
  }
}
