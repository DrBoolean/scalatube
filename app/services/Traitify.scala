package services

import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.json._
import scalaz.Scalaz._
import models.{Assessment}
import scala.concurrent.Future

object Traitify {
  val username = "tcrqlhr95bkov7897uqm9af2a5"

  val assessUrl = "https://api-sandbox.traitify.com/v1/assessments"

  def newAssessment = WS.url(assessUrl).withAuth(username, "x", WSAuthScheme.BASIC).post("{\"deck_id\": \"career-deck\"}")

  def personalityTypes(aid: String) : Future[Response] = {
    val ptypesUrl = s"https://api-sandbox.traitify.com/v1/assessments/$aid/personality_types"
    WS.url(ptypesUrl).withAuth(username, "x", WSAuthScheme.BASIC).get()
  }

  def parseResp(x: Response): Option[Assessment] = Json.parse(x.body).asOpt[Assessment]
}
