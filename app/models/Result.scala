package models

import play.api.libs.json._

case class Personality(name: String)
case class PersonalityBlend(personality_type_1: Personality, personality_type_2: Personality)
case class Result(personality_blend: PersonalityBlend)

object Result {
  implicit val personalityfmt : Format[Personality] = Json.format[Personality]
  implicit val blendfmt : Format[PersonalityBlend] = Json.format[PersonalityBlend]
  implicit val fmt : Format[Result] = Json.format[Result]

  def personality(result: Result) : String = {
    val blend = result.personality_blend
    blend.personality_type_1.name + "/" + blend.personality_type_2.name
  }
}
