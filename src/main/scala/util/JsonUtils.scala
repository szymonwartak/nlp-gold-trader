package util

import spray.json.{JsString, JsValue}


trait JsonUtils {
  implicit class JsonObject(value: JsValue) {
    def \(fieldName: String): JsValue = value.asJsObject.fields.getOrElse(fieldName, JsString("-"))
    def asInt = value.toString().replaceAll("\"","").toInt
    def asString = value.toString().replaceAll("\"","")
    def asLong = value.toString().replaceAll("\"","").toLong
  }


}
