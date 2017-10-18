package libin.parse

import java.io.IOException
import java.util.regex.Pattern

import libin.download.DownloadInfo
import libin.monitor.WanDouJiaMonitor
import libin.utils.{HtmlUtils, PageUtils}
import org.apache.commons.lang3.StringUtils
import org.htmlcleaner.{HtmlCleaner, TagNode}

import scala.collection.mutable

/**
  * Created by baolibin on 2017/10/15.
  * 对每一个网站页面的解析
  */
object ParseWanDouJia {
  val PADDING = ""

  /**
    * 下载豌豆荚的第三步
    * 解析一个豌豆荚页面
    * 输入的URL地址示例：http://www.wandoujia.com/apps/com.qiyi.video
    * 返回一个App的爬取信息
    */
  def parseWanDouJia(content: String, urlPath: String,softwareGame:Boolean): String = {
    try {
      println("====================================== 开始解析页面" + urlPath + "========================================================")
      val htmlCleaner: HtmlCleaner = new HtmlCleaner
      val rootNode: TagNode = htmlCleaner.clean(content)
      val level1CategoryName =if(softwareGame) HtmlUtils.getText(rootNode, "//*[@id=\"j-head-menu\"]/ul/li[2]/a/span").trim
      else HtmlUtils.getText(rootNode, "//*[@id=\"j-head-menu\"]/ul/li[3]/a/span").trim
      println("一级标题:" + level1CategoryName)
      val level2CategoryName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[1]/div[2]/a/span").trim
      println("二级标题:" + level2CategoryName)
      val level3CategoryName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[2]")
        .trim.split("\n").filter(line => StringUtils.isNoneBlank(line)).map(line => line.trim).mkString("|")
      println("三级标题:" + level3CategoryName)
      val appName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[2]/p[1]/span").trim
      println("App名字:" + appName)
      val downloads = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[4]/span[1]/i").trim
      println("下载量:" + downloads)
      //没有采集到会显示暂无,把暂无替换成""
      val favorableRate = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[4]/span[2]/i").trim.replaceAll("暂无", "")
      println("好评率:" + favorableRate)
      val commentNumber = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[4]/div[1]/a/i").trim
      println("评论数:" + commentNumber)
      val comment1 = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[1]/div[2]/p[2]").trim
      println("小编或用户点评1:" + comment1)
      val comment2 = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[1]/div[1]/div").trim
      println("小编或用户点评2:" + comment2)
      val size = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[1]").trim
      println("大小:" + size)
      val updateTime = HtmlUtils.getText(rootNode, "//*[@id=\"baidu_time\"]").trim
      println("更新时间:" + updateTime)
      val versionName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[5]").trim.split(";")(1)
      println("版本:" + versionName)
      val compatibility = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[6]").trim.split("\n")(0)
      println("兼容性:" + compatibility)
      val publisherName = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[7]/span").trim
      println("开发商|作者:" + publisherName)
      val tags = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[1]/dl/dd[3]/div")
        .trim.split("\n").filter(line => StringUtils.isNoneBlank(line)).map(line => line.trim).mkString("|")
      println("标签:" + tags)

      var introduction = ""
      var changeLog = ""
      try {
        val strContent: String = PageUtils.httpUrlSpider(urlPath)
        if (StringUtils.isNotBlank(strContent)) {
          val pat1 = "([\\s\\S]*)itemprop=\"description\">([\\s\\S]*)</div>([\\r\\n\\s]*)<a style=\"display([\\s\\S]*)"
          //val pat2 = "([\\s\\S]*)class=\"con\">([\\s\\S]*)</div>([\\s\\S]*)"
          val pat2 = "([\\s\\S]*)<div data-originheight=\"100\" class=\"con\">([\\s\\S]*)</div>([\\s\\S]*)"
          val compile1 = Pattern.compile(pat1)
          val matcher1 = compile1.matcher(strContent)
          if (matcher1.find) {
            introduction = matcher1.group(2).trim.split("</div> ")(0).replaceAll("</br>", "").replaceAll("<br>", "").replaceAll("<br />", "").replaceAll("<p>", "").replaceAll("</p>", "")
            println("应用信息：" + introduction)
          } else println("应用信息未匹配到!")

          if(strContent.contains("<div data-originheight=\"100\" class=\"con\">")){
            val compile2 = Pattern.compile(pat2)
            val matcher2 = compile2.matcher(strContent)
            if (matcher2.find) {
              changeLog = matcher2.group(2).trim.split("</div> ")(0).replaceAll("</br>", "").replaceAll("<br>", "").replaceAll("<br />", "").replaceAll("<p>", "").replaceAll("</p>", "")
              println("更新内容：" + changeLog)
            } else println("更新内容未匹配到!")
          }else println("更新内容未匹配到!")
        }
        else System.out.println("HttpURLConnection下载页面为空!")
      } catch {
        case e: IOException =>
          println("下载应用信息和更新内容时Connection reset!")
      }
      /*val introduction = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[1]/div[3]/div").trim
      println("应用描述:" + introduction)
      val changeLog = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[1]/div[4]/div").trim
      println("更新内容:" + changeLog)*/
      val relatedDownloads = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[2]/div[2]/ul").trim.split("\n")
        .filter(line => StringUtils.isNoneBlank(line) && !line.contains("下载") && !line.contains("人安装")).map(line => line.trim).mkString("|")
      println("下载了该应用还下载了:" + relatedDownloads)
      //    val historyVersion = HtmlUtils.getText(rootNode, "//div[@class=\"container\"]/div[2]/div[2]/div[1]/div[5]/ul").trim.split("\n")
      //      .filter(line => StringUtils.isNoneBlank(line) && line.contains("版本")).map(line => line.trim).mkString("|")
      //    println("历史版本:" + historyVersion)
      //未下载自定义设定
      val system = "android"
      println("系统:" + system)
      val url = urlPath
      println("url:" + url)
      val appSource = "2" //2表示豌豆荚
      println("数据来自:" + appSource)
      val format = "apk"
      println("格式:" + format)
      //合并App点评
      val comments = if ((comment1 + ";" + comment2).trim == ";") PADDING else comment1 + ";" + comment2

      println("====================================== 页面解析完毕" + urlPath + "========================================================")
      val matchPageContent1 = HtmlUtils.appInfoCrawler1(appName, PADDING, PADDING, downloads, size, versionName, updateTime, publisherName,
        comments, tags, level1CategoryName, level2CategoryName, level3CategoryName, PADDING, PADDING)
      val matchPageContent2 = HtmlUtils.appInfoCrawler2(system, compatibility, PADDING, PADDING, PADDING, url, introduction, changeLog,
        PADDING, PADDING, relatedDownloads, -1, PADDING, commentNumber.toLong, 2, favorableRate, PADDING, PADDING, format)
      HtmlUtils.toAppString1(matchPageContent1) + HtmlUtils.toAppString2(matchPageContent2)
    } catch {
      case _: Throwable => "error"
    }
  }

  /**
    * 下载豌豆荚的第一步
    * 解析安卓软件所有三级分类的URL -> 软件
    * 初始URL地址：http://www.wandoujia.com/category/app
    * 返回所有三级分类的名字以及url
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
    println("安卓软件应用三级url一共:" + rootPath.size)
    rootPath
  }

  /**
    * 下载豌豆荚的第一步
    * 解析游戏所有三级分类的URL -> 游戏
    * 初始URL地址：http://www.wandoujia.com/category/game
    * 返回所有三级分类的名字以及url
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

  /**
    * 下载豌豆荚的第二步
    * 解析分页显示的每个App的具体地址
    * 输入的URL地址示例：http://www.wandoujia.com/category/5029_716/1
    * 返回所有分页App的名字以及url
    */
  def AppPaginationURL(content: String, url: String): mutable.HashMap[String, String] = {
    val rootPath = url
    val urlMap = new mutable.HashMap[String, String]()
    //println(content)
    val htmlCleaner: HtmlCleaner = new HtmlCleaner
    val rootNode: TagNode = htmlCleaner.clean(content)
    //获取当前三级分类应用的页面个数
    var pageNumber = 0
    val linkRegex =
      """ <a class="page-item " href="http://www.wandoujia.com/category/([a-zA-Z0-9/_]*)">([0-9]*)</a>(\D*)<a class="page-item next-page " href="http://www.wandoujia.com/category/([a-zA-Z0-9/_]*)">(\D*)</a> """.trim.r
    for (m <- linkRegex.findAllIn(content).matchData) {
      pageNumber = m.group(2).trim.toInt
    }
    println(url + "的页面个数:" + pageNumber)

    //匹配解析出所有的页面
    for (i <- 1 to pageNumber) {
      val currentPath = rootPath + "/" + i
      println("当前三级分类的应用页面地址：" + currentPath)
      //当前分页页面的内容
      val currentContent: String = DownloadInfo.downloadWanDouJia(currentPath)
      if (StringUtils.isNoneBlank(currentContent)) {
        //使用正则
        val linkRegex =
          """ <h2 class="app-title-h2">(\D*)<a(\D*)href="([a-zA-Z0-9:/._]*)"(\D*)title="(\D*)" class="name"(\D*)>(\D*)</a>(\D*)</h2> """.trim.r
        //所有分页的页面循环下载所有应用的App的URL
        //示例：银河掌控 -> http://www.wandoujia.com/apps/gl.fx.galaxycontrol
        for (m <- linkRegex.findAllIn(currentContent).matchData) {
          urlMap += m.group(7).trim -> m.group(3).trim
        }
        println("应用App的url一共:" + urlMap.size)
      } else {
        println("页面下载失败：" + currentPath)
        WanDouJiaMonitor.saveFaildUrl("当前三级分类的应用页面地址：" + currentPath)
      }
    }
    //println(urlMap.values.mkString("\n"))
    urlMap
  }

}

