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

package helpers

import forms._
import models._
import models.agent._
import play.api.libs.json.JsValue
import services.CacheConstants
import uk.gov.hmrc.domain.Generator
import uk.gov.hmrc.http.cache.client.CacheMap


object IntegrationTestModels {

  import CacheConstants._

  /*
   * this function returns a random nino each time it is called, if you need a constant nino use TestConstants.testNino
   */
  def newNino: String = new Generator().nextNino.nino

  val testStartDate = DateModel("01", "04", "2017")
  val testEndDate = DateModel("01", "04", "2018")
  val testAccountingPeriodPriorCurrent: AccountingPeriodPriorModel = AccountingPeriodPriorModel(AccountingPeriodPriorForm.option_no)
  val testAccountingPeriodPriorNext: AccountingPeriodPriorModel = AccountingPeriodPriorModel(AccountingPeriodPriorForm.option_yes)
  val testAccountingPeriod: AccountingPeriodModel =
    testAccountingPeriod(testStartDate, testEndDate)

  def testAccountingPeriod(startDate: DateModel = testStartDate,
                           endDate: DateModel = testEndDate): AccountingPeriodModel =
    AccountingPeriodModel(startDate, endDate)

  val testBusinessName = BusinessNameModel("test business")
  val testAccountingMethod = AccountingMethodModel(AccountingMethodForm.option_cash)
  val testTerms = TermModel(true)

  val fullKeystoreData: Map[String, JsValue] =
    keystoreData(
      clientDetailsModel = Some(testClientDetails),
      incomeSource = Some(testIncomeSourceBoth),
      otherIncome = Some(testOtherIncomeNo),
      accountingPeriodPrior = Some(testAccountingPeriodPriorCurrent),
      accountingPeriodDate = Some(testAccountingPeriod),
      businessName = Some(testBusinessName),
      accountingMethod = Some(testAccountingMethod),
      terms = Some(testTerms)
    )

  def keystoreData(clientDetailsModel: Option[ClientDetailsModel] = None,
                   incomeSource: Option[IncomeSourceModel] = None,
                   otherIncome: Option[OtherIncomeModel] = None,
                   accountingPeriodPrior: Option[AccountingPeriodPriorModel] = None,
                   accountingPeriodDate: Option[AccountingPeriodModel] = None,
                   businessName: Option[BusinessNameModel] = None,
                   accountingMethod: Option[AccountingMethodModel] = None,
                   terms: Option[TermModel] = None): Map[String, JsValue] = {
    Map.empty[String, JsValue] ++
      clientDetailsModel.map(model => ClientDetails -> ClientDetailsModel.format.writes(model)) ++
      incomeSource.map(model => IncomeSource -> IncomeSourceModel.format.writes(model)) ++
      otherIncome.map(model => OtherIncome -> OtherIncomeModel.format.writes(model)) ++
      accountingPeriodPrior.map(model => AccountingPeriodPrior -> AccountingPeriodPriorModel.format.writes(model)) ++
      accountingPeriodDate.map(model => AccountingPeriodDate -> AccountingPeriodModel.format.writes(model)) ++
      businessName.map(model => BusinessName -> BusinessNameModel.format.writes(model)) ++
      accountingMethod.map(model => AccountingMethod -> AccountingMethodModel.format.writes(model)) ++
      terms.map(model => Terms -> TermModel.format.writes(model))
  }

  lazy val testIncomeSourceBusiness = IncomeSourceModel(IncomeSourceForm.option_business)

  lazy val testIncomeSourceOther = IncomeSourceModel(IncomeSourceForm.option_other)

  lazy val testIncomeSourceProperty = IncomeSourceModel(IncomeSourceForm.option_property)

  lazy val testIncomeSourceBoth = IncomeSourceModel(IncomeSourceForm.option_both)

  lazy val testIsCurrentPeriod = AccountingPeriodPriorModel(AccountingPeriodPriorForm.option_no)

  lazy val testIsNextPeriod = AccountingPeriodPriorModel(AccountingPeriodPriorForm.option_yes)

  lazy val testOtherIncomeNo = OtherIncomeModel(OtherIncomeForm.option_no)

  lazy val testOtherIncomeYes = OtherIncomeModel(OtherIncomeForm.option_yes)

  // we don't verify date of birth since an incorrect one would not result in a match so it can be any date
  lazy val testClientDetails = ClientDetailsModel("Test", "User", IntegrationTestConstants.testNino, testStartDate)

}
