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

package services

import javax.inject.{Inject, Singleton}

import audit.Logging
import auth.IncomeTaxSAUser
import connectors.models.throttling.{CanAccess, UserAccess}
import connectors.throttling.ThrottlingControlConnector
import uk.gov.hmrc.play.http.HeaderCarrier
import utils.Implicits._

import scala.concurrent.Future

@Singleton
class ThrottlingService @Inject()(throttlingControlConnector: ThrottlingControlConnector,
                                  logging: Logging
                                 ) {

  //TODO use another stable identifier for throttling agents when we need the service since we cannot use nino
  def checkAccess(implicit user: IncomeTaxSAUser, hc: HeaderCarrier): Future[Option[UserAccess]] =
    CanAccess


}
