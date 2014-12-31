package services

import play.api.Play.current
import play.api.libs.ws.{WS, Response}
import play.api.libs.json._
import scalaz.Scalaz._
import models.{YoutubeResp, Video}

object YoutubeService {
  def buildQuery(q:String) : String = {
    "http://gdata.youtube.com/feeds/api/videos?q=" |+| q |+| "&alt=json"
  }

  def videos = buildQuery _ andThen WS.url

  def extractEntries = respToYoutube _ andThen YoutubeResp.entries

  def respToYoutube(x: Response): Option[YoutubeResp] = Json.parse(x.body).asOpt[YoutubeResp]
}
