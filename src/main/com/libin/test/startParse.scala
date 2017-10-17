package libin.test

import java.io.{File, PrintWriter, _}
import java.net.{HttpURLConnection, InetSocketAddress, URL}
import java.util.regex.{Matcher, Pattern}

import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.htmlcleaner.{HtmlCleaner, TagNode, XPatherException}
import org.joda.time.DateTime
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}
import org.openqa.selenium.{By, JavascriptExecutor, Proxy}

import scala.collection.mutable

/**
  * Created by baolibin on 17-10-11.
  * 爬取腾讯应用宝数据
  * 软件和游戏解析的内容还不一样
  */
object startParse {
  //获取一级URL的URL
  var urlLevel1 = ""
  var outRootPath = ""
  val urlLevel2_test = "http://android.myapp.com/myapp/category.htm?orgame=1&categoryId=106" //test path
  val urlLevel3_test = "http://android.myapp.com/myapp/detail.htm?apkName=com.jingdong.app.mall" //test path

  val proxyIP = "220.249.185.178"
  val proxyPort = 9999
  val isProxy = false //true表示打开代理,false表示关闭代理.
  val SEPARATE = "\001"
  val INFOSEPARATE = "|"
  val classificationMap = new mutable.HashMap[String, String]() //目录类别
  val PADDING = ""
  val LISTPADDING: List[String] = List()

  val softwareGame = false //true表示抓取软件内容,false表示抓取游戏内容
  val LONGPADDING: Long = -1 //appId和评论数默认值
  val PLATFORM = "android" //平台 android|ios|wp
  val SUPPORTOS = "phone" //phone或mac
  val APPEND = "apk" //安卓：apk , 苹果：ipa ,  windows phone：xap ,三星bada：opa , 黑莓：cod 参考https://zhidao.baidu.com/question/572127412.html
  val CLASSLEVEL1 = "游戏"

  val dateTime: String = new DateTime().minusDays(0).toString("yyyyMMdd") //获取今天的日期

  def initClass(): Unit = {
    if (softwareGame) {
      urlLevel1 = "http://android.myapp.com/myapp/category.htm" //软件的地址
      outRootPath = "/home/baolibin/spider/data/tencentYYB/softwareData/date=" + dateTime + "/" //软件输出目录
      classificationMap += ("url" -> "url")
      classificationMap += ("音乐" -> "music")
      classificationMap += ("安全" -> "security")
      classificationMap += ("健康" -> "health")
      classificationMap += ("视频" -> "video")
      classificationMap += ("社交" -> "socialization")
      classificationMap += ("儿童" -> "children")
      classificationMap += ("工具" -> "tools")
      classificationMap += ("摄影" -> "photography")
      classificationMap += ("教育" -> "education")
      classificationMap += ("通讯" -> "communication")
      classificationMap += ("旅游" -> "tourism")
      classificationMap += ("新闻" -> "news")
      classificationMap += ("出行" -> "travel")
      classificationMap += ("理财" -> "MoneyManagement")
      classificationMap += ("系统" -> "systems")
      classificationMap += ("生活" -> "life")
      classificationMap += ("美化" -> "beautify")
      classificationMap += ("购物" -> "shopping")
      classificationMap += ("阅读" -> "read")
      classificationMap += ("办公" -> "office")
      classificationMap += ("娱乐" -> "entertainment")
    } else {
      urlLevel1 = "http://android.myapp.com/myapp/category.htm?orgame=2" //游戏地址
      outRootPath = "/home/baolibin/spider/data/tencentYYB/gameData/date=" + dateTime + "/" //游戏输出目录
      classificationMap += ("休闲益智" -> "leisurePuzzle")
      classificationMap += ("网络游戏" -> "networkGame")
      classificationMap += ("飞行射击" -> "flightShooting")
      classificationMap += ("动作冒险" -> "actionAdventure")
      classificationMap += ("体育竞速" -> "sportsRacing")
      classificationMap += ("棋牌中心" -> "chessCenter")
      classificationMap += ("经营策略" -> "businessStrategy")
      classificationMap += ("角色扮演" -> "cosplay")
    }
  }

  /**
    * 主函数
    */
  def main(args: Array[String]): Unit = {
    initClass()
    /**
      * 步骤一：
      * 根据应用宝首界面获取一级URL地址
      * (音乐,http://android.myapp.com/myapp/category.htm?orgame=1&categoryId=101)
      * (安全,http://android.myapp.com/myapp/category.htm?orgame=1&categoryId=118)
      */
    //val mapLevel:mutable.HashMap[String, String] = getTitleUrlLevel1(urlLevel1)
    //mapLevel.foreach(println)
    /**
      * 步骤二：
      * 根据应用宝一级URL地址获取二级URL地址
      * http://android.myapp.com/myapp/detail.htm?apkName=com.youyuan.yyhl
      * http://android.myapp.com/myapp/detail.htm?apkName=com.tencent.weishi
      */
    //val mapLevel2: mutable.HashSet[String] = getTitleUrlLevel2(urlLevel2_test)
    //mapLevel2.foreach(println)
    /**
      * 步骤三：
      * 根据二级URL地址爬取具体App页面的内容
      */
    //startParsePage(urlLevel3_test)
    //println(httpUrlSpider(urlLevel3_test))
    startCrawler()
  }

  def startCrawler(): Unit = {
    /**
      * Start crawling
      */
    //下载每一个一级URL
    val mapLevel: mutable.HashMap[String, String] = getTitleUrlLevel1(urlLevel1)
    println(urlLevel1)
    println("一级标题URL个数:" + mapLevel.size)
    mapLevel.foreach(println)
    println()
    //下载每一个二级URL
    for ((k1, v1) <- mapLevel) {
      println("===================================== " + k1 + " 开始爬啦! ================================================")
      val file = new File(outRootPath)
      if (!file.exists()) {
        file.mkdirs()
      }
      val writer = new PrintWriter(new File(outRootPath + classificationMap(k1) + ".txt"))
      val errorUrl = new PrintWriter(new File(outRootPath + classificationMap(k1) + "_errorUrl.txt"))
      println(classificationMap(k1) + ".txt")
      val mapLevel2: mutable.HashSet[String] = getTitleUrlLevel2(v1)
      mapLevel2.foreach(println)
      println()
      //根据二级URL,爬取每个页面的内容
      var count: Int = 0
      val length: Int = mapLevel2.size
      for (v2 <- mapLevel2) {
        //val content: mutable.HashMap[String, String] = startParsePage(v2)
        count += 1
        val pageInfo: String = startParsePage(v2)
        val title = pageInfo.split("\001")(1)
        val url = pageInfo.split("\001")(20)
        if (!pageInfo.contains("Connection reset")) {
          writer.println(pageInfo)
          println("=====================================" + title + " 已经爬完啦! ================================================")
          println("正在爬取" + title + "应用App的数据," + k1 + "分类已经爬完" + count + "条记录啦,一共" + length + "个App数据!")
        } else {
          errorUrl.println(url)
          println("=====================================" + title + " 爬取失败,已放进失败库! ================================================")
          println("正在爬取" + title + "应用App的数据,爬取失败," + k1 + "分类已经爬完" + count + "条记录啦,一共" + length + "个App数据!")
        }
        println()
        Thread.sleep(2000)
      }
      println("===================================== " + k1 + " 已经爬完啦!" + " =====================================")
      println()
      Thread.sleep(5000)
      writer.close()
      errorUrl.close()
    }
  }

  /**
    * 获取一级URL地址
    */
  def getTitleUrlLevel1(rootUrl: String): mutable.HashMap[String, String] = {
    val mapLevel: mutable.HashMap[String, String] = new mutable.HashMap[String, String]
    val proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyIP, proxyPort))
    val u: URL = new URL(rootUrl)
    //是否打开代理
    val conn: HttpURLConnection = if (isProxy) u.openConnection(proxy).asInstanceOf[HttpURLConnection]
    else u.openConnection().asInstanceOf[HttpURLConnection]

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
    * 获取二级URL地址
    */
  def getTitleUrlLevel2(url2: String): mutable.HashSet[String] = {
    val reSet = new mutable.HashSet[String]()
    //设置代理
    val proxyIpAndPort = proxyIP + ":" + proxyPort.toString
    val cap = new DesiredCapabilities
    val proxy = new Proxy
    proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort)
    cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true)
    cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true)
    System.setProperty("http.nonProxyHosts", "localhost")
    cap.setCapability(CapabilityType.PROXY, proxy)

    //用Selenium动态加载的页面数据的抓取
    System.getProperties.setProperty("webdriver.chrome.driver", "/home/baolibin/spider/chromedriver")
    //System.getProperties.setProperty("webdriver.chrome.driver", "E:\\chromedriver.exe")
    //是否打开代理
    val webDriver = if (isProxy) new ChromeDriver(cap)
    else new ChromeDriver()

    webDriver.get(url2)
    scrollHeightUrl(webDriver)
    scrollHeightUrl(webDriver)
    scrollHeightUrl(webDriver)
    scrollHeightUrl(webDriver)
    scrollHeightUrl(webDriver)
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

  /**
    * 根据二级URL,开始爬取一页的信息
    */
  def startParsePage(url: String): String = {
    var matchPageContent1: appInfoCrawler1 = null
    var matchPageContent2: appInfoCrawler2 = null
    val request = new HttpGet(url)
    //设置代理
    val proxy = new HttpHost(proxyIP, proxyPort)
    val requestConfig = RequestConfig.custom.setProxy(proxy).build
    //是否打开代理
    if (isProxy)
      request.setConfig(requestConfig)
    request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/61.0.3163.79 Chrome/61.0.3163.79 Safari/537.36")
    //爬取一个页面的内容
    val builder = HttpClients.custom
    val client = builder.build
    var content = ""
    try {
      val response = client.execute(request)
      val entity = response.getEntity
      content = EntityUtils.toString(entity)
    } catch {
      case e: IOException =>
        println("获取" + url + "页面内容下载失败!")
    }
    //对爬取的页面进行解析
    System.out.println("url：" + url)
    //System.out.println(content)
    val htmlCleaner = new HtmlCleaner
    if (content != null) {
      val rootNode = htmlCleaner.clean(content)
      //对页面进行解析
      //获取标题
      val appName = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[1]/div[1]")
      System.out.println("标题：" + appName)
      //获取评分
      val score = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[2]/div[2]")
      System.out.println("评分：" + score)
      //获取下载量
      val download = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[3]/div[1]")
      System.out.println("下载量：" + download)
      //获取大小
      val size = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[3]/div[3]")
      System.out.println("大小：" + size)
      //分类
      val classification = getText(rootNode, "//*[@id=\"J_DetCate\"]")
      System.out.println("分类：" + classification)
      //版本号
      val versionId = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[3]/div[2]")
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
      val developers = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[3]/div[6]")
      System.out.println("开发商：" + developers)
      //相关应用1
      val r1 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[1]/div[2]/a[1]")
      System.out.println("相关应用1：" + r1)
      //相关应用2
      val r2 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[2]/div[2]/a[1]")
      System.out.println("相关应用2：" + r2)
      //相关应用3
      val r3 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[3]/div[2]/a[1]")
      System.out.println("相关应用3：" + r3)
      //相关应用4
      val r4 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[4]/div[2]/a[1]")
      System.out.println("相关应用4：" + r4)
      //同一开发者1
      val d1 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[1]/div[2]/a[1]")
      System.out.println("同一开发者1：" + d1)
      //同一开发者2
      val d2 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[2]/div[2]/a[1]")
      System.out.println("同一开发者2：" + d2)
      //同一开发者3
      val d3 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[3]/div[2]/a[1]")
      System.out.println("同一开发者3：" + d3)
      //同一开发者4
      val d4 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[4]/div[2]/a[1]")
      System.out.println("同一开发者4：" + d4)
      //使用HttpURLConnection进行下载
      var updateInfo = ""
      var updateContent = ""
      try {
        val strContent: String = httpUrlSpider(url)
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
      r.add(r1)
      r.add(r2)
      r.add(r3)
      r.add(r4)
      r.remove("")

      val d = new mutable.HashSet[String]()
      d.add(d1)
      d.add(d2)
      d.add(d3)
      d.add(d4)
      d.remove("")

      matchPageContent1 = appInfoCrawler1(appName, PADDING, score, download, size, versionId, updateTime, developers,
        PADDING, LISTPADDING, CLASSLEVEL1, classification, PADDING, PADDING, PADDING)
      matchPageContent2 = appInfoCrawler2(PLATFORM, PADDING, PADDING, PADDING, PADDING, url, updateInfo, updateContent,
        r.toList, d.toList, LISTPADDING, -1, SUPPORTOS, -1, 1, PADDING, LISTPADDING, PADDING, APPEND)
    }
    println(toAppString1(matchPageContent1) + toAppString2(matchPageContent2))
    toAppString1(matchPageContent1) + toAppString2(matchPageContent2)
  }

  /**
    * 用Js滑道窗口底部
    */
  def scrollHeightUrl(webDriver: ChromeDriver): Unit = {
    webDriver.asInstanceOf[JavascriptExecutor].executeScript("window.scrollTo(0, document.body.scrollHeight)")
    Thread.sleep(4000)
  }

  /**
    * 使用HttpURLConnection进行下载页面
    */
  def httpUrlSpider(url: String): String = {
    val proxy: java.net.Proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyIP, proxyPort))
    val u = new URL(url)
    //是否打开代理
    val conn = if (isProxy) u.openConnection(proxy).asInstanceOf[HttpURLConnection]
    else u.openConnection().asInstanceOf[HttpURLConnection]

    val sb = new StringBuilder
    val stream: InputStream = conn.getInputStream
    val bufferedReader: BufferedReader = new BufferedReader(new InputStreamReader(stream, "utf-8"))
    var line = bufferedReader.readLine
    while (line != null) {
      sb.append(line)
      line = bufferedReader.readLine
    }
    sb.toString
  }

  /**
    * 获取指定标签的值
    */
  def getText(rootNode: TagNode, xpath: String): String = {
    var result = ""
    try {
      val evaluateXPath = rootNode.evaluateXPath(xpath)
      if (evaluateXPath.nonEmpty) {
        val tagNode = evaluateXPath(0).asInstanceOf[TagNode]
        result = tagNode.getText.toString
      }
    } catch {
      case e: XPatherException =>
        println("xpath信息获取失败!")
    }
    result
  }

  /**
    * 匹配数据工场的一条数据
    */
  case class appInfoCrawler1(appName: String,
                             packageName: String,
                             score: String,
                             downloads: String,
                             size: String,
                             versionName: String,
                             updateTime: String,
                             publisherName: String,
                             comment: String,
                             tags: List[String],
                             level1CategoryName: String,
                             level2CategoryName: String,
                             level3CategoryName: String,
                             language: String,
                             safety: String
                            )

  case class appInfoCrawler2(
                              system: String,
                              compatibility: String,
                              postage: String,
                              officialEdition: String,
                              ad: String,
                              url: String,
                              introduction: String,
                              changeLog: String,
                              relatedApplications: List[String],
                              sameDeveloper: List[String],
                              relatedDownloads: List[String],
                              appId: Long,
                              support: String,
                              commentNumber: Long,
                              appSource: Int,
                              favorableRate: String,
                              historyVersion: List[String],
                              networking: String,
                              format: String
                            )

  /**
    * 返回数据工场的一条表数据
    * 最多22个参数
    */
  def toAppString1(appInfi: appInfoCrawler1): String = {
    val sb = new mutable.StringBuilder()
    sb.append(appInfi.appName)
    sb.append(SEPARATE + appInfi.packageName)
    sb.append(SEPARATE + appInfi.score)
    sb.append(SEPARATE + appInfi.downloads)
    sb.append(SEPARATE + appInfi.size)
    sb.append(SEPARATE + appInfi.versionName)
    sb.append(SEPARATE + appInfi.updateTime)
    sb.append(SEPARATE + appInfi.publisherName)
    sb.append(SEPARATE + appInfi.comment)
    sb.append(SEPARATE + "")
    sb.append(SEPARATE + appInfi.level1CategoryName)
    sb.append(SEPARATE + appInfi.level2CategoryName)
    sb.append(SEPARATE + appInfi.level3CategoryName)
    sb.append(SEPARATE + appInfi.language)
    sb.append(SEPARATE + appInfi.safety)
    sb.toString()
  }

  def toAppString2(appInfi: appInfoCrawler2): String = {
    val sb = new mutable.StringBuilder()
    sb.append(SEPARATE + appInfi.system)
    sb.append(SEPARATE + appInfi.compatibility)
    sb.append(SEPARATE + appInfi.postage)
    sb.append(SEPARATE + appInfi.officialEdition)
    sb.append(SEPARATE + appInfi.ad)
    sb.append(SEPARATE + appInfi.url)
    sb.append(SEPARATE + appInfi.introduction)
    sb.append(SEPARATE + appInfi.changeLog)
    if (appInfi.relatedApplications.nonEmpty)
      sb.append(SEPARATE + appInfi.relatedApplications.mkString(INFOSEPARATE))
    else sb.append(SEPARATE + "")
    if (appInfi.sameDeveloper.nonEmpty)
      sb.append(SEPARATE + appInfi.sameDeveloper.mkString(INFOSEPARATE))
    else sb.append(SEPARATE + "")
    sb.append(SEPARATE + "")
    sb.append(SEPARATE + "")
    sb.append(SEPARATE + appInfi.support)
    sb.append(SEPARATE + "")
    sb.append(SEPARATE + appInfi.appSource.toString)
    sb.append(SEPARATE + appInfi.favorableRate)
    sb.append(SEPARATE + "")
    sb.append(SEPARATE + appInfi.networking)
    sb.append(SEPARATE + appInfi.format)
    sb.toString()
  }

}
