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

package audit.models

import audit.{AuditModel, AuditType}
import models.agent.ClientDetailsModel

object ClientMatchingAuditing {
  val clientMatchingTransactionName = "ITSAMatchingRequest"
  sealed trait ClientMatchingAuditType extends AuditType

  case class ClientMatchingAuditModel(auditType: ClientMatchingAuditType, arn: String, clientDetailsModel: ClientDetailsModel) extends AuditModel {
    override val transactionName: String = clientMatchingTransactionName
    override val detail: Map[String, String] = Map(
      "arn" -> arn,
      "firstName" -> clientDetailsModel.firstName,
      "lastName" -> clientDetailsModel.lastName,
      "nino" -> clientDetailsModel.ninoInBackendFormat,
      "dateOfBirth" -> clientDetailsModel.dateOfBirth.toOutputDateFormat
    )
  }

  case object ClientMatchingRequest extends ClientMatchingAuditType

  case object ClientMatchingFailure extends ClientMatchingAuditType

  case object ClientMatchingSuccess extends ClientMatchingAuditType

}