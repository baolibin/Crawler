package libin.download

import libin.utils.PageUtils

/**
  * Created by baolibin on 2017/10/15.
  */
object DownloadInfo {
  /**
    * 下载豌豆荚的页面
    */
  def downloadWanDouJia(url: String): String = {
    val page = PageUtils.getHttpClientContent(url)
    page
  }
}
