package libin.startCrawl

import java.io.File

import libin.download.DownloadInfo
import libin.parse.ParseWanDouJia
import libin.utils.StoreUtils
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime

import scala.collection.mutable

/**
  * Created by baolibin on 2017/10/15.
  * 爬取豌豆荚数据,爬取分2大类：软件和游戏
  * 爬取页面地址：
  * http://www.wandoujia.com/category/app
  * http://www.wandoujia.com/category/game
  */
object WanDouJia {

  val softwareGame = true //true表示抓取软件内容,false表示抓取游戏内容

  //开始下载的初始地址
  val initPathSoftware = "http://www.wandoujia.com/category/app"
  val initPathGame = "http://www.wandoujia.com/category/game"
  val classificationMap = new mutable.HashMap[String, String]() //目录类别

  def main(args: Array[String]) {
    initWanDouJiaCategory()
    /**
      * 下载豌豆荚第一步
      * 示例URL：http://www.wandoujia.com/category/6002_996
      * 下载豌豆荚三级分类的软件和游戏的URL地址
      */
    val wanDouJiaPageLevel3: String = if (softwareGame) DownloadInfo.downloadWanDouJia(initPathSoftware) else DownloadInfo.downloadWanDouJia(initPathGame)
    //获取软件应用的App
    val setLevel3: mutable.HashMap[String, String] = ParseWanDouJia.parseWanDouJiaSoftware(wanDouJiaPageLevel3)
    println("软件应用三级url一共:" + setLevel3.size)
    println(setLevel3.mkString("\n"))

    /**
      * 下载豌豆荚的第二步
      * 示例URL：http://www.wandoujia.com/category/6002_996/2
      * 处理每一个三级分类页面,包含很多页面,解析每个三级URL地址里的分页应用的URL
      */
    val dateTime = new DateTime().minusDays(0).toString("yyyyMMdd")
    var count = 0
    for ((nameLenel3, url3) <- setLevel3) {
      count += 1
      println("\n================ 开始下载三级分类App" + nameLenel3 + ", url=" + url3 + " ========= 正在爬第:" + count + "个, ==== 一共" + setLevel3.size + "个============================")
      //拼接输出目录
      val fileName = if (classificationMap.contains(nameLenel3)) classificationMap(nameLenel3) else "no"
      //val outRootPath = "/home/baolibin/spider/crawler/crawlerData/WanDouJia/date=" + dateTime
      val outRootPath = "E://_github_2017/crawlerData/WanDouJia/date=" + dateTime

      if (softwareGame) {
        val file = new File(outRootPath + "/software/")
        if (!file.exists()) {
          file.mkdirs()
        }
      } else {
        val file = new File(outRootPath + "/game/")
        if (!file.exists()) {
          file.mkdirs()
        }
      }

      val writer = if (softwareGame) {
        StoreUtils.getWriter(outRootPath + "/software/" + fileName + ".txt")
      } else {
        StoreUtils.getWriter(outRootPath + "/game/" + fileName + ".txt")
      }
      val writerFailed = if (softwareGame) {
        StoreUtils.getWriter(outRootPath + "/software/" + fileName + "_error.txt")
      } else {
        StoreUtils.getWriter(outRootPath + "/game/" + fileName + "_error.txt")
      }
      //获取三级页面的内容
      val wanDouJiaAppUrl: String = DownloadInfo.downloadWanDouJia(url3)
      if (StringUtils.isNoneBlank(wanDouJiaAppUrl)) {
        //解析出三级页面的页数,并解析每个页面的所有App内容  urlMap为返回所有分页的URL
        val urlMap = ParseWanDouJia.AppPaginationURL(wanDouJiaAppUrl, url3)
        println(urlMap.mkString("\n"))
        var countJ = 0
        //对每个分页里面的App数据进行爬取
        if (urlMap.nonEmpty) {
          for ((nameLenel4, url4) <- urlMap) {
            countJ += 1
            println("\n===== 开始下载三级分类App里的" + nameLenel4 + ", url=" + url4 + " ========= 正在爬第:" + countJ + "个, ==== 一共" + urlMap.size + "个============================")
            /**
              * 解析豌豆荚页面
              */
            //对每一个页面的内容进行抓取
            val wanDouJiaPageContent: String = DownloadInfo.downloadWanDouJia(url4)
            if (StringUtils.isNoneBlank(wanDouJiaPageContent)) {
              //解析豌豆荚页面
              val WanDouJiaPageParse = ParseWanDouJia.parseWanDouJia(wanDouJiaPageContent, url4)
              if (StringUtils.isNoneBlank(WanDouJiaPageParse) && WanDouJiaPageParse != "error") {
                //println(WanDouJiaPageParse)
                //写入App爬取的内容
                writer.println(WanDouJiaPageParse)
              } else {
                println("具体App页面内容下载失败:" + url4)
                writerFailed.println("具体App页面内容下载失败:" + url4)
              }
            } else {
              println("具体App页面内容下载失败:" + url4)
              writerFailed.println("具体App页面内容下载失败:" + url4)
            }
            println("===== 已爬完三级分类App里的" + nameLenel4 + ", url=" + url4 + " ========= 已爬完第:" + countJ + "个, ==== 一共" + urlMap.size + "个============================\n")
            //每解析一个App页面内容停顿2秒
            Thread.sleep(2000)
          }
        } else {
          println("该三级页面的分页个数为空：" + url3)
          writerFailed.println("该三级页面的分页个数为空：" + url3)
        }
      } else {
        println("获取该三级页面内容为空：" + url3)
        writerFailed.println("获取该三级页面内容为空：" + url3)
      }
      //关闭流
      StoreUtils.closeWriter(writer)
      StoreUtils.closeWriter(writerFailed)
      println("================ 已爬完三级分类App" + nameLenel3 + ", url=" + url3 + " ========= 正在爬第:" + count + "个, ==== 一共" + setLevel3.size + "个============================\n")
      //下载每一个三级分类停顿的时间
      Thread.sleep(5000)
    }
  }

  /**
    * 标记存储的文件名
    */
  def initWanDouJiaCategory(): Unit = {
    classificationMap += ("空战" -> "kongZhan")
    classificationMap += ("桌面" -> "zhuoMian")
    classificationMap += ("地图导航" -> "diTuDaoHang")
    classificationMap += ("音乐" -> "yinYue")
    classificationMap += ("射击" -> "sheJi")
    classificationMap += ("解谜" -> "jieMi")
    classificationMap += ("驾考" -> "jiaKao")
    classificationMap += ("日韩系" -> "riHanXi")
    classificationMap += ("纸牌" -> "zhiPai")
    classificationMap += ("斗地主" -> "douDiZhu")
    classificationMap += ("学习" -> "xueXi")
    classificationMap += ("公交地铁" -> "gongJiaoDiTie")
    classificationMap += ("角色扮演" -> "jueSeBanYan")
    classificationMap += ("讲故事" -> "jiangGuShi")
    classificationMap += ("私密" -> "siMi")
    classificationMap += ("休闲" -> "xiuXian")
    classificationMap += ("卡牌" -> "kaPai")
    classificationMap += ("武侠" -> "wuXia")
    classificationMap += ("省电" -> "shengDian")
    classificationMap += ("美食" -> "meiShi")
    classificationMap += ("麻将" -> "maJiang")
    classificationMap += ("直播" -> "zhiBo")
    classificationMap += ("团购" -> "tuanGOu")
    classificationMap += ("求职" -> "qiuZhi")
    classificationMap += ("早教" -> "zaoJiao")
    classificationMap += ("翻译" -> "fanYi")
    classificationMap += ("相册" -> "xiangCe")
    classificationMap += ("优惠" -> "youHui")
    classificationMap += ("背单词" -> "beiDanCi")
    classificationMap += ("捕鱼" -> "buYu")
    classificationMap += ("月经" -> "yueJing")
    classificationMap += ("购票" -> "gouPiao")
    classificationMap += ("投资" -> "touZi")
    classificationMap += ("益智" -> "yiZhi")
    classificationMap += ("篮球" -> "lanQiu")
    classificationMap += ("玄幻" -> "xuanHuan")
    classificationMap += ("Root" -> "root")
    classificationMap += ("搞怪" -> "gaoGuai")
    classificationMap += ("赛车" -> "saiChe")
    classificationMap += ("足球" -> "zuQiu")
    classificationMap += ("攻略" -> "gongLue")
    classificationMap += ("考试" -> "kaoShi")
    classificationMap += ("理财记账" -> "liCaiJiZhang")
    classificationMap += ("优化" -> "youHua")
    classificationMap += ("交友" -> "jiaoYou")
    classificationMap += ("玩游戏" -> "wanYouXi")
    classificationMap += ("怀孕" -> "huanYun")
    classificationMap += ("医疗" -> "yiLiao")
    classificationMap += ("大型" -> "daXing")
    classificationMap += ("婚恋" -> "huiLian")
    classificationMap += ("社区" -> "sheQu")
    classificationMap += ("小说" -> "xiaoShuo")
    classificationMap += ("锁屏" -> "suoPing")
    classificationMap += ("旅行攻略" -> "luXingGongLue")
    classificationMap += ("唱儿歌" -> "changErGe")
    classificationMap += ("枪战" -> "qiangZhan")
    classificationMap += ("搞笑" -> "gaoXiao")
    classificationMap += ("动漫" -> "dongMan")
    classificationMap += ("棋类" -> "qiLei")
    classificationMap += ("大作" -> "daZuo")
    classificationMap += ("小儿百科" -> "xiaoErBaiKe")
    classificationMap += ("漫画" -> "manHua")
    classificationMap += ("用车租车" -> "yongCheZuChe")
    classificationMap += ("格斗" -> "geDou")
    classificationMap += ("银行" -> "yinHang")
    classificationMap += ("跑酷" -> "paoKu")
    classificationMap += ("铃声" -> "lingSheng")
    classificationMap += ("单机" -> "danJi")
    classificationMap += ("策略" -> "ceLue")
    classificationMap += ("支付" -> "zhiFu")
    classificationMap += ("跳跃" -> "tiaoYue")
    classificationMap += ("壁纸" -> "biZhi")
    classificationMap += ("K歌" -> "kGe")
    classificationMap += ("狙击" -> "zuJi")
    classificationMap += ("浏览器" -> "liuLanQi")
    classificationMap += ("RPG" -> "rpg")
    classificationMap += ("修仙" -> "xiuXian")
    classificationMap += ("办公软件" -> "banGongRuanJian")
    classificationMap += ("商城" -> "shangCheng")
    classificationMap += ("男生" -> "nanSheng")
    classificationMap += ("聊天" -> "liaoTian")
    classificationMap += ("字体" -> "ziTi")
    classificationMap += ("效率办公" -> "xiaoLvBanGong")
    classificationMap += ("新闻资讯" -> "xinWenZiXun")
    classificationMap += ("上门服务" -> "shangMenFuWu")
    classificationMap += ("经营" -> "jingYing")
    classificationMap += ("育儿" -> "yuEr")
    classificationMap += ("电话通讯" -> "dianHuaTongXun")
    classificationMap += ("炒股" -> "chaoGu")
    classificationMap += ("短视频" -> "duanShiPin")
    classificationMap += ("摩托" -> "moTuo")
    classificationMap += ("房产家居" -> "fangChanJiaJu")
    classificationMap += ("模拟" -> "moNi")
    classificationMap += ("动作射击" -> "dongZuoSheJi")
    classificationMap += ("即时" -> "jiShi")
    classificationMap += ("娱乐" -> "yuLe")
    classificationMap += ("都市" -> "douShi")
    classificationMap += ("运动" -> "yueDong")
    classificationMap += ("全球导购" -> "quanQiuDaoGou")
    classificationMap += ("封神" -> "fengShen")
    classificationMap += ("街机" -> "jieJi")
    classificationMap += ("仙侠" -> "xianXia")
    classificationMap += ("电影票" -> "dianYingPiao")
    classificationMap += ("输入法" -> "shuRuFa")
    classificationMap += ("汽车" -> "qiChe")
    classificationMap += ("主题" -> "zhuTi")
    classificationMap += ("笔记" -> "biJi")
    classificationMap += ("彩票" -> "caiPiao")
    classificationMap += ("消除" -> "xiaoChu")
    classificationMap += ("邮箱" -> "youXiang")
    classificationMap += ("坦克" -> "tanKe")
    classificationMap += ("小工具" -> "xiaoGongJu")
    classificationMap += ("相机" -> "xiangJi")
    classificationMap += ("视频" -> "shiPin")
    classificationMap += ("竞速" -> "jingSu")
    classificationMap += ("魔幻" -> "moHuan")
    classificationMap += ("WiFi" -> "wifi")
    classificationMap += ("减肥健身" -> "jianFeiJianShen")
    classificationMap += ("历史" -> "liShi")
    classificationMap += ("竞技策略" -> "jingJiCeLue")
    classificationMap += ("动态壁纸" -> "dongTaiBiZhi")
    classificationMap += ("收音机" -> "shouYinJi")
    classificationMap += ("美化" -> "meiHua")
    classificationMap += ("保险" -> "baoXian")
    classificationMap += ("冒险" -> "maoXian")
    classificationMap += ("极限" -> "jiXian")
    classificationMap += ("游戏助手" -> "youXiZhuShou")
    classificationMap += ("模拟器" -> "moNiQi")
    classificationMap += ("养成" -> "yangCheng")
    classificationMap += ("快递" -> "kuaiDi")
    classificationMap += ("借贷" -> "jieDai")
    classificationMap += ("躲避" -> "duoBi")
    classificationMap += ("战争" -> "zhanZheng")
    classificationMap += ("听书" -> "tingShu")
    classificationMap += ("桌游" -> "zhuoYou")
    classificationMap += ("云盘存储" -> "yunPanCunChu")
    classificationMap += ("养生" -> "yangSheng")
    classificationMap += ("横版" -> "hengBan")
    classificationMap += ("飞行" -> "feiXing")
    classificationMap += ("其他球类" -> "qiTaQiuLei")
    classificationMap += ("电子书" -> "dianZiShu")
    classificationMap += ("西游" -> "xiYou")
    classificationMap += ("修改器" -> "xiuGaiQi")
    classificationMap += ("图像编辑" -> "tuXiangBianJi")
    classificationMap += ("文件管理" -> "wenJianGuanLi")
    classificationMap += ("回合" -> "huiHe")
    classificationMap += ("英语" -> "yingYu")
    classificationMap += ("塔防" -> "taFang")
    classificationMap += ("桌球" -> "zhuoQiu")
    classificationMap += ("女生" -> "nvSheng")
    classificationMap += ("桌面部件" -> "zhuoMianBuJian")
    classificationMap += ("安全" -> "anQuan")
    classificationMap += ("三国" -> "sanGuo")
    classificationMap += ("住宿" -> "zhuSu")
  }
}
