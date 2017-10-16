package libin.startCrawl

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
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}
import org.openqa.selenium.{By, JavascriptExecutor, Proxy}

import scala.collection.mutable

/**
  * Created by baolibin on 17-10-11.
  */
object startParse {
  //获取一级URL的URL
  var urlLevel1 = "http://android.myapp.com/myapp/category.htm"
  val urlLevel2_test = "http://android.myapp.com/myapp/category.htm?orgame=1&categoryId=106"
  //test path
  val urlLevel3_test = "http://android.myapp.com/myapp/detail.htm?apkName=com.jingdong.app.mall" //test path

  val proxyIP = "220.249.185.178"
  //"220.249.185.178"
  val proxyPort = 9999
  // 9999
  val isProxy = false
  //true表示打开代理,false表示关闭代理.
  val SEPARATE = "\001"

  val classificationMap = new mutable.HashMap[String, String]()

  def initClass(): Unit = {
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
      * (健康,http://android.myapp.com/myapp/category.htm?orgame=1&categoryId=109)
      * (视频,http://android.myapp.com/myapp/category.htm?orgame=1&categoryId=103)
      */
    //val mapLevel:mutable.HashMap[String, String] = getTitleUrlLevel1(urlLevel1)
    //mapLevel.foreach(println)

    /**
      * 步骤二：
      * 根据应用宝一级URL地址获取二级URL地址
      * http://android.myapp.com/myapp/detail.htm?apkName=com.youyuan.yyhl
      * http://android.myapp.com/myapp/detail.htm?apkName=com.tencent.weishi
      * http://android.myapp.com/myapp/detail.htm?apkName=com.longzhu.tga
      * http://android.myapp.com/myapp/detail.htm?apkName=com.lanjingren.ivwen
      */
    //val mapLevel2: mutable.HashSet[String] = getTitleUrlLevel2(urlLevel2_test)
    //mapLevel2.foreach(println)

    /**
      * 步骤三：
      * 根据二级URL地址爬取具体App页面的内容
      */
    //startParsePage(urlLevel3_test)
    /**
      * Start crawling
      */
    //下载每一个一级URL
    val mapLevel: mutable.HashMap[String, String] = getTitleUrlLevel1(urlLevel1)
    mapLevel.foreach(println)
    println()
    //下载每一个二级URL
    for ((k1, v1) <- mapLevel) {
      println("===================================== " + k1 + " 开始爬啦! ================================================")
      val writer = new PrintWriter(new File(classificationMap(k1) + ".txt"))
      val errorUrl = new PrintWriter(new File(classificationMap(k1) + "_errorUrl.txt"))
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
        val url = pageInfo.split("\001")(0)
        if (!pageInfo.contains("Connection reset")) {
          writer.println(pageInfo)
          println("=====================================" + title + " 已经爬完啦! ================================================")
          println("正在爬取" + title + "应用App的数据," + k1 + "分类已经爬完" + count + "条记录啦,还差" + length + "个App数据未爬取!")
        } else {
          errorUrl.println(url)
          println("=====================================" + title + " 爬取失败,已放进失败库! ================================================")
          println("正在爬取" + title + "应用App的数据,爬取失败," + k1 + "分类已经爬完" + count + "条记录啦,还差" + length + "个App数据未爬取!")
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
        val p: String = "(\\D*)<a href=\"([a-zA-Z0-9?&=-]+)\">(\\D*)</a></li>"
        val r1: Pattern = Pattern.compile(p)
        val m1: Matcher = r1.matcher(line)
        if (m1.find) {
          if (m1.group(3).length == 2) mapLevel.put(m1.group(3), rootUrl + m1.group(2))
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
    //System.getProperties.setProperty("webdriver.chrome.driver", "/home/baolibin/spider/chromedriver")
    System.getProperties.setProperty("webdriver.chrome.driver", "E:\\chromedriver.exe")
    //是否打开代理
    val webDriver = if (isProxy) new ChromeDriver(cap)
    else new ChromeDriver()

    webDriver.get(url2)
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
    val pageContent: mutable.HashMap[String, String] = new mutable.HashMap[String, String]()
    val sb = new mutable.StringBuilder()
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
    pageContent += ("url" -> url)
    sb.append("url:" + url)
    //System.out.println(content)
    val htmlCleaner = new HtmlCleaner
    if (content != null) {
      val rootNode = htmlCleaner.clean(content)
      //对页面进行解析
      //获取标题
      val title = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[1]/div[1]")
      System.out.println("标题：" + title)
      pageContent += ("title" -> title)
      sb.append(SEPARATE + "title:" + title)
      //获取评分
      val score = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[2]/div[2]")
      System.out.println("评分：" + score)
      pageContent += ("score" -> score)
      sb.append(SEPARATE + "score:" + score)
      //获取下载量
      val download = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[3]/div[1]")
      System.out.println("下载量：" + download)
      pageContent += ("download" -> download)
      sb.append(SEPARATE + "download:" + download)
      //获取大小
      val size = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[1]/div[2]/div[3]/div[3]")
      System.out.println("大小：" + size)
      pageContent += ("size" -> size)
      sb.append(SEPARATE + "size:" + size)
      //分类
      val classification = getText(rootNode, "//*[@id=\"J_DetCate\"]")
      System.out.println("分类：" + classification)
      pageContent += ("classification" -> classification)
      sb.append(SEPARATE + "classification:" + classification)
      //版本号
      val versionId = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[3]/div[2]")
      System.out.println("版本号：" + versionId)
      pageContent += ("versionId" -> versionId)
      sb.append(SEPARATE + "versionId:" + versionId)
      //更新时间
      val p = "(\\D*)data-apkPublishTime=\"([0-9]+)\">(\\D*)"
      val c1 = Pattern.compile(p)
      val m1 = c1.matcher(content)
      if (m1.find) {
        System.out.println("更新时间：" + m1.group(2))
        pageContent += ("updateTime" -> m1.group(2))
        sb.append(SEPARATE + "updateTime:" + m1.group(2))
      }
      else {
        System.out.println("NO MATCH")
        sb.append(SEPARATE + "updateTime:" + null)
      }
      //开发商
      val developers = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[3]/div[6]")
      System.out.println("开发商：" + developers)
      pageContent += ("developers" -> developers)
      sb.append(SEPARATE + "developers:" + developers)
      //相关应用1
      val r1 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[1]/div[2]/a[1]")
      System.out.println("相关应用1：" + r1)
      pageContent += ("r1" -> r1)
      sb.append(SEPARATE + "r1:" + r1)
      //相关应用2
      val r2 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[2]/div[2]/a[1]")
      System.out.println("相关应用2：" + r2)
      pageContent += ("r2" -> r2)
      sb.append(SEPARATE + "r2:" + r2)
      //相关应用3
      val r3 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[3]/div[2]/a[1]")
      System.out.println("相关应用3：" + r3)
      pageContent += ("r3" -> r3)
      sb.append(SEPARATE + "r3:" + r3)
      //相关应用4
      val r4 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[5]/ul/li[4]/div[2]/a[1]")
      System.out.println("相关应用4：" + r4)
      pageContent += ("r4" -> r4)
      sb.append(SEPARATE + "r4:" + r4)
      //同一开发者1
      val d1 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[1]/div[2]/a[1]")
      System.out.println("同一开发者1：" + d1)
      pageContent += ("d1" -> d1)
      sb.append(SEPARATE + "d1:" + d1)
      //同一开发者2
      val d2 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[2]/div[2]/a[1]")
      System.out.println("同一开发者2：" + d2)
      pageContent += ("d2" -> d2)
      sb.append(SEPARATE + "d2:" + d2)
      //同一开发者3
      val d3 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[3]/div[2]/a[1]")
      System.out.println("同一开发者3：" + d3)
      pageContent += ("d3" -> d3)
      sb.append(SEPARATE + "d3:" + d3)
      //同一开发者4
      val d4 = getText(rootNode, "//*[@id=\"J_DetDataContainer\"]/div/div[6]/ul/li[4]/div[2]/a[1]")
      System.out.println("同一开发者4：" + d4)
      pageContent += ("d4" -> d4)
      sb.append(SEPARATE + "d4:" + d4)
      //使用HttpURLConnection进行下载
      try {
        val strContent: String = httpUrlSpider(url)
        if (StringUtils.isNotBlank(strContent)) {
          val pat = "([\\s\\S]*)<div class=\"det-app-data-info\">([\\s\\S]*)<div class=\"det-app-data-tit([\\s\\S]*)<div class=\"det-app-data-info\">([\\s\\S]*)<a class=\"det-intro-showmore([\\s\\S]*)"
          val compile = Pattern.compile(pat)
          val matcher = compile.matcher(strContent)
          if (matcher.find) {
            System.out.println("应用信息：" + matcher.group(2).trim)
            System.out.println("更新内容：" + matcher.group(4).trim)
            pageContent += ("updateInfo" -> matcher.group(2).trim)
            pageContent += ("updateContent" -> matcher.group(4).trim)
            sb.append(SEPARATE + "updateInfo:" + matcher.group(2).trim)
            sb.append(SEPARATE + "updateContent:" + matcher.group(4).trim)
          }
          else {
            sb.append(SEPARATE + "updateInfo:" + null)
            sb.append(SEPARATE + "updateContent:" + null)
          }
        }
        else System.out.println("HttpURLConnection下载页面为空!")
      } catch {
        case e: IOException =>
          println("下载应用信息和更新内容时Connection reset!")
          sb.append(SEPARATE + "updateInfo:" + "Connection reset")
          sb.append(SEPARATE + "updateInfo:" + "Connection reset")
      }
    }
    //pageContent
    println(sb.toString())
    sb.toString()
  }

  /**
    * 用Js滑道窗口底部
    */
  def scrollHeightUrl(webDriver: ChromeDriver): Unit = {
    webDriver.asInstanceOf[JavascriptExecutor].executeScript("window.scrollTo(0, document.body.scrollHeight)")
    Thread.sleep(5000)
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
}
