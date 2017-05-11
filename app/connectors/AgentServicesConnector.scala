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

package connectors

import javax.inject.Inject

import audit.Logging
import config.AppConfig
import play.api.http.Status
import play.api.libs.json.JsBoolean
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpResponse, InternalServerException}

import scala.concurrent.Future._
import scala.concurrent.{ExecutionContext, Future}

class AgentServicesConnector @Inject()(appConfig: AppConfig,
                                       httpGet: HttpGet,
                                       logging: Logging)(implicit ec: ExecutionContext) extends RawResponseReads {
  def agentClientURL(nino: String): String = s"${appConfig.agentServicesUrl}/agentClientRelationship/$nino"

  def parsingFailure(status: Int, body: String): Throwable =
    new InternalServerException(s"AgentServicesConnector.isPreExistingRelationship unexpected response from agent services: status=$status body=$body")

  def isPreExistingRelationship(nino: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = agentClientURL(nino)

    httpGet.GET(url).flatMap {
      case HttpResponse(Status.OK, JsBoolean(value), _, _) => successful(value)
      case HttpResponse(status, _, _, body) => failed(parsingFailure(status, body))
    }
  }
}
