package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import anorm._
import anorm.SqlParser._

case class Assessment(id: Pk[Long], aid: String, name: String, email: String)

object Assessment {

  implicit val rds: Reads[Assessment] = ( (__ \ "id").read[String]).map(Assessment.apply(NotAssigned, _, "", ""))

  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("aid") ~
    get[String]("name") ~
    get[String]("email") map {
      case id~aid~name~email => Assessment(id, aid, name, email)
    }
  }

  def findAll(): Seq[Assessment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from assessments").as(Assessment.simple *)
    }
  }

  def findByEmail(email: String): Option[Assessment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from assessments WHERE email = {email}").on('email -> email).as(Assessment.simple *).headOption
    }
  }

  def create(assessment: Assessment): String = {
    DB.withConnection { implicit connection =>
      SQL("insert into assessments(aid, name, email) values ({aid}, {name}, {email})").on(
        'aid -> assessment.aid,
        'email -> assessment.email,
        'name -> assessment.name
      ).executeUpdate()
    }
    assessment.aid
  }
}
