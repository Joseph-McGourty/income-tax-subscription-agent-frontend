/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package testonly.connectors

import javax.inject.{Inject, Singleton}

import audit.Logging
import connectors.RawResponseReads
import play.api.http.Status._
import play.api.libs.json.Json
import testonly.TestOnlyAppConfig
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future


case class Value(value: String)

object Value {
  implicit val format = Json.format[Value]
}

case class UserData(nino: Value = Value("AA111111A"),
                    sautr: Value = Value("1234567890"),
                    firstName: Value = Value("Test"),
                    lastName: Value = Value("User"),
                    dob: Value = Value("01011980"))

object UserData {
  implicit val format = Json.format[UserData]
}

case class Request(
                    data: UserData,
                    testId: String = "1234567",
                    name: String = "CID",
                    service: String = "find",
                    resultCode: Option[Int] = Some(200),
                    delay: Option[Int] = None,
                    timeToLive: Option[Int] = Some(120000 * 60)
                  )

object Request {
  implicit val format = Json.format[Request]
}

import utils.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MatchingStubConnector @Inject()(appConfig: TestOnlyAppConfig,
                                      http: WSHttp,
                                      logging: Logging) extends RawResponseReads {

  lazy val dynamicStubUrl = appConfig.matchingStubsURL + "/dynamic-cid"

  lazy val temp = "http://localhost:9353/matching/find?nino=AA111111A"

  def newUser(userData: UserData)(implicit hc: HeaderCarrier): Future[Boolean] = {
    http.POST[Request, HttpResponse](dynamicStubUrl, Request(userData)).flatMap {
      response =>
        response.status match {
          case CREATED =>
            logging.debug("MatchingStubConnector.newUser successful")
            true
          case status =>
            logging.debug(s"MatchingStubConnector.newUser failure: status=$status, body=${response.body}")
            false
        }
    }
  }

}
