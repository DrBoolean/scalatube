package models
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Video(id: String, title: String)
case class Feed(entry: Seq[Video])
case class YoutubeResp(feed: Feed)

object Video {
  implicit val rds: Reads[Video] = (
      (__ \ "id").read[Map[String, String]].map{(m: Map[String, String]) => m.get("$t").head} and
      (__ \ "title").read[Map[String, String]].map{(m: Map[String, String]) => m.get("$t").head}
    )(Video.apply(_, _))

  implicit val wts: Writes[Video] = (
      (__ \ "id").write[String] and
      (__ \ "title").write[String]
    )(unlift(Video.unapply))

  implicit val fmt: Format[Video] = Format(rds, wts)

  def getId(video: Video) : String = {
    video.id.split("/").last
  }
}

object Feed {
  implicit val feedFormat : Format[Feed] = Json.format[Feed]
}

object YoutubeResp {
  implicit val youtubeFormat : Format[YoutubeResp] = Json.format[YoutubeResp]
}

