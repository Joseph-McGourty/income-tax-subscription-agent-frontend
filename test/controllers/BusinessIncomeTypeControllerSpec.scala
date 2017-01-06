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

package controllers

import akka.actor._
import akka.stream._
import assets.MessageLookup
import auth._
import config.{FrontendAppConfig, FrontendAuthConnector}
import org.jsoup.Jsoup
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._

class BusinessIncomeTypeControllerSpec extends PlaySpec with OneAppPerTest {

  object TestBusinessIncomeTypeController extends BusinessIncomeTypeController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val postSignInRedirectUrl = MockConfig.ggSignInContinueUrl
  }

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "The BusinessIncomeType controller" should {
    "use the correct applicationConfig" in {
      BusinessIncomeTypeController.applicationConfig must be (FrontendAppConfig)
    }
    "use the correct authConnector" in {
      BusinessIncomeTypeController.authConnector must be (FrontendAuthConnector)
    }
    "use the correct postSignInRedirectUrl" in {
      BusinessIncomeTypeController.postSignInRedirectUrl must be (FrontendAppConfig.ggSignInContinueUrl)
    }
  }

  "Calling the showBusinessIncomeType action of the BusinessIncomeType with an authorised user" should {

    lazy val result = TestBusinessIncomeTypeController.showBusinessIncomeType(authenticatedFakeRequest())

    "return unimplemented (501)" in {
      status(result) must be (Status.NOT_IMPLEMENTED)
    }
  }

  "Calling the showBusinessIncomeType action of the BusinessIncomeType with an unauthorised user" should {

    lazy val result = TestBusinessIncomeTypeController.showBusinessIncomeType(FakeRequest())

    "return 303" in {
      status(result) must be (Status.SEE_OTHER)
    }
  }

  "Calling the submitBusinessIncomeType action of the BusinessIncomeType with an authorised user" should {

    lazy val result = TestBusinessIncomeTypeController.submitBusinessIncomeType(authenticatedFakeRequest())

    "return unimplemented (501)" in {
      status(result) must be (Status.NOT_IMPLEMENTED)
    }
  }

  "Calling the submitBusinessIncomeType action of the BusinessIncomeType with an unauthorised user" should {

    lazy val result = TestBusinessIncomeTypeController.submitBusinessIncomeType(FakeRequest())

    "return 303" in {
      status(result) must be (Status.SEE_OTHER)
    }
  }
}
