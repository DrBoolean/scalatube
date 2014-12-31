package controllers

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import services.YoutubeService

object Youtube extends Controller {

  def index() = Action.async { req =>
    val query = req.queryString.get("q").map(_.head).getOrElse("cats")
    val future_entries = YoutubeService.videos(query).get().map(YoutubeService.extractEntries)
    future_entries.map(x => Ok(views.html.index(x)))
  }
}
