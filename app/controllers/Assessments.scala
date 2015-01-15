package controllers

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.ws.{Response}
import scala.concurrent.Future
import play.api.mvc.{Action, Controller}
import models.{Assessment, Result}
import services.Traitify
import anorm.NotAssigned
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, email}

object Assessments extends Controller {

  val userForm = Form(tuple("name" -> nonEmptyText, "email" -> email))

  def index() = Action { req =>
    req.session.get("current_assessment").map { a_id =>
      Redirect(routes.Assessments.show())
    }.getOrElse {
      Ok(views.html.index(userForm))
    }
  }

  def list() = Action {
    val assess = Assessment.findAll()
    Ok(views.html.list(assess))
  }

  def done() = Action {
    Redirect(routes.Assessments.index()).withNewSession
  }

  def results() = Action.async {
    val assess = Assessment.findAll()
    val fs = assess.map( x => Traitify.personalityTypes(x.aid).map(y => (x, y.body)))
    Future.sequence(fs.toList).map { rs =>
      val results : Seq[(String, String, String)] = rs.map { r =>
        val o_res2 = Json.parse(r._2).asOpt[Result]
        (r._1.name, r._1.email, o_res2.map(Result.personality(_)).getOrElse("nope"))
      }
      Ok(views.html.results(results))
    }
  }

  def show() = Action { req =>
    req.session.get("current_assessment").map { a_id =>
      Ok(views.html.show(a_id))
    }.getOrElse {
      Redirect(routes.Assessments.index())
    }
  }

  def create() = Action.async { implicit req =>
    userForm.bindFromRequest.fold(
      errors => Future(Ok(views.html.index(errors))),
        {
          case (name, email) => {
            makeAssessment(name, email).map { aid =>
              Redirect(routes.Assessments.index()).withSession("current_assessment" -> aid)
            }
          }
        }
      )
  }

  def parseTraitifyResp(x:Response) : Assessment = Json.parse(x.body).as[Assessment]

  def makeAssessment(name: String, email: String) : Future[String] = {
    Assessment.findByEmail(email).map(x => Future(x.aid)).getOrElse {
      Traitify.newAssessment.map(parseTraitifyResp).map { a =>
        Assessment.create(Assessment(NotAssigned, a.aid, name, email))
      }
    }
  }
}
