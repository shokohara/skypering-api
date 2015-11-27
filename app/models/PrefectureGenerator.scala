package models

import java.io.{File, FileOutputStream}

import scala.util.Properties

object PrefectureGenerator {

  val header =
    """package models
      |
      |import play.api.data.validation.ValidationError
      |import play.api.libs.json._
      |
      |import scala.util.control.Exception._
      |
      |abstract sealed trait Prefecture {
      |  val CODE: String
      |}
    """.stripMargin

  def d(a: String, b: String) = s"""  case $a => $b${Properties.lineSeparator}"""

  def c(a: String, b: String) = s"""  case e if e == $a => $b${Properties.lineSeparator}"""

  lazy val toPrefectureContent = (seed.map(_._1).map(_ + ".toString") zip seed.map(_._1)).map { case (a, b) => c(a, b)}.reduce(_ + _)

  lazy val codeToPrefecture = (seed.map(_._1).map(_ + ".CODE") zip seed.map(_._1)).map { case (a, b) => d(a, b)}.reduce(_ + _)

  lazy val prefectureList = "List(" + seed.map(_._1).map("," + _).reduce(_ + _).tail + ")"

  val objectPrefecture = s"""object Prefecture {
    |  implicit val writes: Writes[Prefecture] = new Writes[Prefecture] {
    |    def writes(d: Prefecture): JsValue = JsString(d.toString)
    |  }
    |
    |  implicit val reads = new Reads[Prefecture] {
    |    def reads(json: JsValue): JsResult[Prefecture] = json match {
    |      case JsString(d) => allCatch opt toPrefecture(d) map { p: Prefecture => JsSuccess(p)} getOrElse (JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.prefecture")))))
    |      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.prefecture"))))
    |    }
    |  }
    |
    |  def toPrefecture(string: String): Prefecture = string match {
    |$toPrefectureContent
    |  }
    |
    |  def codeToPrefecture(string: String): Prefecture = string match {
    |$codeToPrefecture
    |  }
    |
    |  val prefectures = $prefectureList
    |}
  """.stripMargin

  lazy val seed = Array(
    ("Hokkaido", "北海道", "JP-01"),
    ("Aomori", "青森県", "JP-02"),
    ("Iwate", "岩手県", "JP-03"),
    ("Miyagi", "宮城県", "JP-04"),
    ("Akita", "秋田県", "JP-05"),
    ("Yamagata", "山形県", "JP-06"),
    ("Fukushima", "福島県", "JP-07"),
    ("Ibaraki", "茨城県", "JP-08"),
    ("Tochigi", "栃木県", "JP-09"),
    ("Gunma", "群馬県", "JP-10"),
    ("Saitama", "埼玉県", "JP-11"),
    ("Chiba", "千葉県", "JP-12"),
    ("Tokyo", "東京都", "JP-13"),
    ("Kanagawa", "神奈川県", "JP-14"),
    ("Niigata", "新潟県", "JP-15"),
    ("Toyama", "富山県", "JP-16"),
    ("Ishikawa", "石川県", "JP-17"),
    ("Fukui", "福井県", "JP-18"),
    ("Yamanashi", "山梨県", "JP-19"),
    ("Nagano", "長野県", "JP-20"),
    ("Gifu", "岐阜県", "JP-21"),
    ("Shizuoka", "静岡県", "JP-22"),
    ("Aichi", "愛知県", "JP-23"),
    ("Mie", "三重県", "JP-24"),
    ("Shiga", "滋賀県", "JP-25"),
    ("Kyoto", "京都府", "JP-26"),
    ("Osaka", "大阪府", "JP-27"),
    ("Hyogo", "兵庫県", "JP-28"),
    ("Nara", "奈良県", "JP-29"),
    ("Wakayama", "和歌山県", "JP-30"),
    ("Tottori", "鳥取県", "JP-31"),
    ("Shimane", "島根県", "JP-32"),
    ("Okayama", "岡山県", "JP-33"),
    ("Hiroshima", "広島県", "JP-34"),
    ("Yamaguchi", "山口県", "JP-35"),
    ("Tokushima", "徳島県", "JP-36"),
    ("Kagawa", "香川県", "JP-37"),
    ("Ehime", "愛媛県", "JP-38"),
    ("Kochi", "高知県", "JP-39"),
    ("Fukuoka", "福岡県", "JP-40"),
    ("Saga", "佐賀県", "JP-41"),
    ("Nagasaki", "長崎県", "JP-42"),
    ("Kumamoto", "熊本県", "JP-43"),
    ("Oita", "大分県", "JP-44"),
    ("Miyazaki", "宮崎県", "JP-45"),
    ("Kagoshima", "鹿児島県", "JP-46"),
    ("Okinawa", "沖縄県", "JP-47")
  )

  val prefectures = (Array(header) ++ Array(objectPrefecture) ++ seed.map(code)).reduce(_ + _)

  def code(nvc: (String, String, String)): String = code(nvc._1, nvc._2, nvc._3)

  def code(n: String, v: String, c: String): String = {
    s"""
      |case object $n extends Prefecture {
      |  val CODE = "$c"
      |
      |  override def toString: String = "$v"
      |}
    """.stripMargin
  }

  def run = {
    val file = new File("./Prefecture.scala")
    val fos = new FileOutputStream(file)
    fos.write(prefectures.getBytes)
    fos.close()
  }

}
