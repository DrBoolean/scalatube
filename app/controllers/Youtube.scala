package controllers

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import anorm.NotAssigned
import scala.concurrent.Future
import models._
import play.api.libs.ws.{WS, Response}
import play.api.libs.json._
import scalaz.Scalaz._

object Youtube extends Controller {

  def buildQuery(q:String) : String = {
    "http://gdata.youtube.com/feeds/api/videos?q=" |+| q |+| "&alt=json"
  }

  def call = buildQuery _ andThen WS.url

  def respToYoutube(x: Response): Option[YoutubeResp] = Json.parse(x.body).asOpt[YoutubeResp]

  def getEntry(x:Option[YoutubeResp]): Seq[Video] = x match {
        case Some(yt) => yt.feed.entry;
        case None => List(Video("nope", "0"));
      }

  def index() = Action.async { request =>
    val query = request.queryString.get("q").map((q: Seq[String]) => q.head).getOrElse("cats")
    val future_entries = call(query).get().map (respToYoutube _ andThen getEntry)
    future_entries.map { x => Ok(views.html.index(x))}
  }
}

