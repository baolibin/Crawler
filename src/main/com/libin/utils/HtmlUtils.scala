package libin.utils

import org.apache.commons.lang3.StringUtils
import org.htmlcleaner.{TagNode, XPatherException}
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver

import scala.collection.mutable

/**
  * Created by baolibin on 2017/10/15.
  * 解析一个HTML页面的工具类
  */
object HtmlUtils {
  val SEPARATE = "\001"
  val INFOSEPARATE = "|"

  /**
    * 获取xPath的内容
    */
  def getText(rootNode: TagNode, xpath: String): String = {
    var result: String = null
    try {
      val evaluateXPath: Array[AnyRef] = rootNode.evaluateXPath(xpath)
      if (evaluateXPath.length > 0) {
        val tagNode: TagNode = evaluateXPath(0).asInstanceOf[TagNode]
        result = tagNode.getText.toString
      }
    } catch {
      case _: Throwable => result = null
    }
    result
  }

  /**
    * 用Js滑道窗口底部
    */
  def scrollHeightUrl(webDriver: ChromeDriver): Unit = {
    webDriver.asInstanceOf[JavascriptExecutor].executeScript("window.scrollTo(0, document.body.scrollHeight)")
    Thread.sleep(4000)
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
                             tags: String,
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
                              relatedApplications: String,
                              sameDeveloper: String,
                              relatedDownloads: String,
                              appId: Long,
                              support: String,
                              commentNumber: Long,
                              appSource: Int,
                              favorableRate: String,
                              historyVersion: String,
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
    if (StringUtils.isNoneBlank(appInfi.packageName)) sb.append(SEPARATE + appInfi.packageName) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.score)) sb.append(SEPARATE + appInfi.score) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.downloads)) sb.append(SEPARATE + appInfi.downloads) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.size)) sb.append(SEPARATE + appInfi.size) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.versionName)) sb.append(SEPARATE + appInfi.versionName) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.updateTime)) sb.append(SEPARATE + appInfi.updateTime) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.publisherName)) sb.append(SEPARATE + appInfi.publisherName) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.comment)) sb.append(SEPARATE + appInfi.comment) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.tags)) sb.append(SEPARATE + appInfi.tags) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.level1CategoryName)) sb.append(SEPARATE + appInfi.level1CategoryName) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.level2CategoryName)) sb.append(SEPARATE + appInfi.level2CategoryName) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.level3CategoryName)) sb.append(SEPARATE + appInfi.level3CategoryName) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.language)) sb.append(SEPARATE + appInfi.language) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.safety)) sb.append(SEPARATE + appInfi.safety) else sb.append(SEPARATE + "")
    sb.toString()
  }

  def toAppString2(appInfi: appInfoCrawler2): String = {
    val sb = new mutable.StringBuilder()
    if (StringUtils.isNoneBlank(appInfi.system)) sb.append(SEPARATE + appInfi.system) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.compatibility)) sb.append(SEPARATE + appInfi.compatibility) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.postage)) sb.append(SEPARATE + appInfi.postage) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.officialEdition)) sb.append(SEPARATE + appInfi.officialEdition) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.ad)) sb.append(SEPARATE + appInfi.ad) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.url)) sb.append(SEPARATE + appInfi.url) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.introduction)) sb.append(SEPARATE + appInfi.introduction) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.changeLog)) sb.append(SEPARATE + appInfi.changeLog) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.relatedApplications)) sb.append(SEPARATE + appInfi.relatedApplications) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.sameDeveloper)) sb.append(SEPARATE + appInfi.sameDeveloper) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.relatedDownloads)) sb.append(SEPARATE + appInfi.relatedDownloads) else sb.append(SEPARATE + "")
    if (appInfi.appId != -1) sb.append(SEPARATE + appInfi.appId) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.support)) sb.append(SEPARATE + appInfi.support) else sb.append(SEPARATE + "")
    if (appInfi.commentNumber != -1) sb.append(SEPARATE + appInfi.commentNumber) else sb.append(SEPARATE + "")
    if (appInfi.appSource > 0) sb.append(SEPARATE + appInfi.appSource) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.favorableRate)) sb.append(SEPARATE + appInfi.favorableRate) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.historyVersion)) sb.append(SEPARATE + appInfi.historyVersion) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.networking)) sb.append(SEPARATE + appInfi.networking) else sb.append(SEPARATE + "")
    if (StringUtils.isNoneBlank(appInfi.format)) sb.append(SEPARATE + appInfi.format) else sb.append(SEPARATE + "")
    sb.toString()
  }
}
