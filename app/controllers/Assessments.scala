package controllers

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scala.concurrent.Future
import play.api.mvc.{Action, Controller}
import models.Assessment
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

  def results() = Action.async {
    val assess = Assessment.findAll()
    val fs = assess.map( x => Traitify.personalityTypes(x.aid).map(_.body))
    Future.sequence(fs.toList).map { rs =>
      Ok(Json.toJson(rs)).as("application/json")
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
          case (name, email) =>{
            val ass = Assessment(NotAssigned, "", name, email)
            Traitify.newAssessment.map(Traitify.parseResp).map { o_assess =>
              o_assess.map { assess =>
                Assessment.create(Assessment(NotAssigned, assess.aid, name, email))
                Redirect(routes.Assessments.index()).withSession(
                  "current_assessment" -> assess.aid
                )
              }.getOrElse(BadRequest)
            }
          }
        }
      )

  }
}
