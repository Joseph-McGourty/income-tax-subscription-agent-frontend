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

package audit

import javax.inject.Inject

import audit.AuditingService.toDataEvent
import play.api.Configuration
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.{Audit, DataEvent}
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class AuditingService @Inject()(configuration: Configuration,
                                auditConnector: AuditConnector){
  lazy val appName: String = configuration.getString("appName").get //Will fail if appName does not exists

  def audit(auditModel: AuditModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit =
    auditConnector.sendEvent(toDataEvent(appName, auditModel))
}

object AuditingService {
  def toDataEvent(appName: String, auditModel: AuditModel)(implicit hc: HeaderCarrier): DataEvent = {
    val auditType: String = auditModel.auditType
    val transactionName: String = auditModel.transactionName
    val path: String = "N/A"
    val detail: Map[String, String] = auditModel.detail
    val tags: Map[String, String] = Map.empty[String, String]

    DataEvent(
      auditSource = appName,
      auditType = auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(transactionName, path) ++ tags,
      detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(detail.toSeq: _*)
    )
  }
}

trait AuditModel {
  val transactionName: String
  val detail: Map[String, String]
  val auditType: String
}

