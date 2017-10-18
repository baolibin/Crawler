package libin.parse

import java.io.{BufferedReader, IOException, InputStream, InputStreamReader}
import java.net.{HttpURLConnection, URL}
import java.util.regex.{Matcher, Pattern}

import libin.utils.{HtmlUtils, PageUtils}
import org.apache.commons.lang3.StringUtils
import org.htmlcleaner.HtmlCleaner
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver

import scala.collection.mutable

/**
  * Created by baolibin on 17-10-18.
  * 对每一个网站页面的解析
  */
object ParseTencentYYB {
  /**
    * 下载腾讯应用宝的第三步
    * 爬取腾讯应用宝一页的数据
    * content:该App页面的内容
    * url:要爬取的App的URL
    * CLASSLEVEL1：该App所属的一级分类
    * 返回一个App的爬取信息
    */
  def parseYYB(content: String, url: String, CLASSLEVEL1: String): String = {
    val PADDING = ""
    //try {
    //对爬取的页面进行解析
    System.out.println("url：" + url)
    val htmlCleaner = new HtmlCleaner
    val rootNode = htmlCleaner.clean(content)
    //获取标题
    val appName = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[1]/div[1]")
    System.out.println("标题：" + appName)
    //获取评分
    val score = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[2]/div[2]")
    System.out.println("评分：" + score)
    //获取下载量
    val download = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[3]/div[1]")
    System.out.println("下载量：" + download)
    //获取大小
    val size = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[3]/div[3]")
    System.out.println("大小：" + size)
    //分类
    val classification = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetCate\"]")
    System.out.println("分类：" + classification)
    //版本号
    val versionId = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[3]/div[2]")
    System.out.println("版本号：" + versionId)
    //更新时间
    var updateTime = ""
    val p = "(\\D*)data-apkPublishTime=\"([0-9]+)\">(\\D*)"
    val c1 = Pattern.compile(p)
    val m1 = c1.matcher(content)
    if (m1.find) {
      System.out.println("更新时间：" + m1.group(2))
      updateTime = m1.group(2)
    }
    else {
      System.out.println("NO MATCH")
    }
    //开发商
    val developers = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[3]/div[6]")
    System.out.println("开发商：" + developers)
    //相关应用1
    val r1 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[1]/div[2]/a[1]")
    System.out.println("相关应用1：" + r1)
    //相关应用2
    val r2 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[2]/div[2]/a[1]")
    System.out.println("相关应用2：" + r2)
    //相关应用3
    val r3 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[3]/div[2]/a[1]")
    System.out.println("相关应用3：" + r3)
    //相关应用4
    val r4 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[4]/div[2]/a[1]")
    System.out.println("相关应用4：" + r4)
    //同一开发者1
    val d1 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[1]/div[2]/a[1]")
    System.out.println("同一开发者1：" + d1)
    //同一开发者2
    val d2 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[2]/div[2]/a[1]")
    System.out.println("同一开发者2：" + d2)
    //同一开发者3
    val d3 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[3]/div[2]/a[1]")
    System.out.println("同一开发者3：" + d3)
    //同一开发者4
    val d4 = HtmlUtils.getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[4]/div[2]/a[1]")
    System.out.println("同一开发者4：" + d4)
    //使用HttpURLConnection进行下载
    var updateInfo = ""
    var updateContent = ""
    try {
      val strContent: String = PageUtils.httpUrlSpider(url)
      //println(strContent)
      if (StringUtils.isNotBlank(strContent)) {
        val pat = "([\\s\\S]*)<div class=\"det-app-data-info\">([\\s\\S]*)</div>([\\s]*)<div class=\"det-app-data-tit([\\s\\S]*)<div class=\"det-app-data-info\">([\\s\\S]*)</div>([\\s]*)</div>([\\s]*)<a class=\"det-intro-showmore([\\s\\S]*)"
        val compile = Pattern.compile(pat)
        val matcher = compile.matcher(strContent)
        if (matcher.find) {
          System.out.println("应用信息：" + matcher.group(2).trim.replaceAll("</br>", ""))
          System.out.println("更新内容：" + matcher.group(5).trim.replaceAll("</br>", ""))
          updateInfo = matcher.group(2).trim.replaceAll("</br>", "")
          updateContent = matcher.group(5).trim.replaceAll("</br>", "")
        }
      }
      else System.out.println("HttpURLConnection下载页面为空!")
    } catch {
      case e: IOException =>
        println("下载应用信息和更新内容时Connection reset!")
        updateInfo = "Connection reset"
        updateContent = "Connection reset"
    }
    val r = new mutable.HashSet[String]()
    if (StringUtils.isNoneBlank(r1)) r.add(r1)
    if (StringUtils.isNoneBlank(r2)) r.add(r2)
    if (StringUtils.isNoneBlank(r3)) r.add(r3)
    if (StringUtils.isNoneBlank(r4)) r.add(r4)
    r.remove("")
    val d = new mutable.HashSet[String]()
    if (StringUtils.isNoneBlank(d1)) r.add(d1)
    if (StringUtils.isNoneBlank(d2)) r.add(d2)
    if (StringUtils.isNoneBlank(d3)) r.add(d3)
    if (StringUtils.isNoneBlank(d4)) r.add(d4)
    d.remove("")

    val matchPageContent1 = HtmlUtils.appInfoCrawler1(appName, PADDING, score, download, size, versionId, updateTime, developers,
      PADDING, PADDING, CLASSLEVEL1, classification, PADDING, PADDING, PADDING)
    val matchPageContent2 = HtmlUtils.appInfoCrawler2("android", PADDING, PADDING, PADDING, PADDING, url, updateInfo, updateContent,
      r.toList.mkString("|"), d.toList.mkString("|"), PADDING, -1, "phone", -1, 1, PADDING, PADDING, PADDING, "apk")
    println(HtmlUtils.toAppString1(matchPageContent1) + HtmlUtils.toAppString2(matchPageContent2))
    HtmlUtils.toAppString1(matchPageContent1) + HtmlUtils.toAppString2(matchPageContent2)
    //} catch {
    //  case e: IOException => "error"
    //  case _ => "error"
    //}
  }

  /**
    * 下载腾讯应用宝的第一步
    * 获取一级URL地址
    * rootUrl:爬取页面的根目录,比如：http://android.myapp.com/myapp/category.htm?orgame=1&categoryId=102
    * classificationMap：所有分类的类名,用于过滤掉无效的分类
    * 返回三级分类的所有名字以及url
    */
  def getTitleUrlLevel1(rootUrl: String, classificationMap: mutable.HashMap[String, String]): mutable.HashMap[String, String] = {
    val mapLevel: mutable.HashMap[String, String] = new mutable.HashMap[String, String]
    val u: URL = new URL(rootUrl)
    val conn: HttpURLConnection = u.openConnection().asInstanceOf[HttpURLConnection]

    val stream: InputStream = conn.getInputStream
    val bufferedReader: BufferedReader = new BufferedReader(new InputStreamReader(stream, "utf-8"))
    var line: String = bufferedReader.readLine
    while (line != null) {
      if (line.contains("categoryId")) {
        //println(line)
        val p: String = "(\\D*)<a href=\"([a-zA-Z0-9?&=-]+)\">(\\D*)</a></li>"
        val r1: Pattern = Pattern.compile(p)
        val m1: Matcher = r1.matcher(line)
        if (m1.find) {
          if (classificationMap.contains(m1.group(3))) mapLevel.put(m1.group(3), rootUrl + m1.group(2))
        }
        else System.out.println("NO MATCH")
      }
      line = bufferedReader.readLine
    }
    mapLevel
  }


  /**
    * 下载腾讯应用宝的第二步
    * 获取二级URL地址
    * url2:每一个应用App的地址,比如：http://android.myapp.com/myapp/detail.htm?apkName=com.bytetech1
    * 返回每一个App的url
    */
  def getTitleUrlLevel2(url2: String): mutable.HashSet[String] = {
    val reSet = new mutable.HashSet[String]()
    //用Selenium动态加载的页面数据的抓取
    System.getProperties.setProperty("webdriver.chrome.driver", "/home/baolibin/spider/chromedriver")
    //System.getProperties.setProperty("webdriver.chrome.driver", "E:\\chromedriver.exe")
    //是否打开代理
    val webDriver = new ChromeDriver()

    webDriver.get(url2)
    HtmlUtils.scrollHeightUrl(webDriver)
    HtmlUtils.scrollHeightUrl(webDriver)
    HtmlUtils.scrollHeightUrl(webDriver)
    HtmlUtils.scrollHeightUrl(webDriver)
    HtmlUtils.scrollHeightUrl(webDriver)
    val webElement = webDriver.findElement(By.xpath("/html"))
    val content = webElement.getAttribute("outerHTML")
    //System.out.println(content)

    //用正则表达式解析出URL
    val linkRegex =
      """ (src|href)="([^"]+)"|(src|href)='([^']+)' """.trim.r
    val list = for (m <- linkRegex.findAllIn(content).matchData if m.group(2).contains("detail.htm")) yield {
      m.group(2)
    }
    val perStr = "http://android.myapp.com/myapp/"
    val set = list.toSet
    println("一共下载的App页面URL个数：" + set.size)
    set.foreach { line =>
      reSet += (perStr + line)
    }
    webDriver.close()
    reSet
  }

}
