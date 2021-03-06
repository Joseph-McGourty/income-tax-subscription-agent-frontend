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

package utils

import common.Constants
import models.DateModel
import uk.gov.hmrc.domain.Generator

object TestConstants {
  /*
  * this nino is a constant, if you need a fresh one use TestModels.newNino
  */
  lazy val testNino = new Generator().nextNino.nino
  lazy val testMTDID = new Generator().nextAtedUtr.utr //Not a valid MTDID, for test purposes only
  lazy val startDate = DateModel("05", "04", "2017")
  lazy val endDate = DateModel("04", "04", "2018")
  lazy val ggServiceName = Constants.ggServiceName
  lazy val agentServiceName = Constants.agentServiceName
  lazy val testARN = new Generator().nextAtedUtr.utr //Not a valid ARN, for test purposes only
}
