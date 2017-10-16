package libin.utils

import java.io.{InputStreamReader, BufferedReader, InputStream, IOException}
import java.net.{HttpURLConnection, URL}

import org.apache.http.HttpEntity
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder, HttpClients}
import org.apache.http.util.EntityUtils

/**
  * Created by baolibin on 2017/10/15.
  * 下载页面的工具类
  */
object PageUtils {
  /**
    * 使用HttpClients下载一个页面的HTML内容
    * 下载成功返回页面内容
    * 下载失败返回失败标识符
    */
  def getHttpClientContent(url: String): String = {
    val builder: HttpClientBuilder = HttpClients.custom
    val client: CloseableHttpClient = builder.build
    var content: String = null
    val request: HttpGet = new HttpGet(url)
    request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/61.0.3163.79 Chrome/61.0.3163.79 Safari/537.36")
    try {
      val response: CloseableHttpResponse = client.execute(request)
      val entity: HttpEntity = response.getEntity
      content = EntityUtils.toString(entity)
    } catch {
      case e: ClientProtocolException => content = "ClientProtocolException"
      case e: IOException => content = "IOException"
    }
    content
  }

  /**
    * 使用HttpURLConnection进行下载页面
    */
  def httpUrlSpider(url: String): String = {
    val sb = new StringBuilder
    val u = new URL(url)
    val conn = u.openConnection().asInstanceOf[HttpURLConnection]
    val stream: InputStream = conn.getInputStream
    val bufferedReader: BufferedReader = new BufferedReader(new InputStreamReader(stream, "utf-8"))
    var line = bufferedReader.readLine
    while (line != null) {
      sb.append(line)
      line = bufferedReader.readLine
    }
    sb.toString
  }
}
