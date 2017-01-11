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

import auth._
import config.{FrontendAppConfig, FrontendAuthConnector}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

class ContactEmailControllerSpec extends ControllerBaseSpec {

  override val controllerName: String = "ContactEmailControllerSpec"
  override val authorisedRoutes: Map[String, Action[AnyContent]] = Map(
    "showContactEmail" -> TestContactEmailController.showContactEmail,
    "submitContactEmail" -> TestContactEmailController.submitContactEmail
  )

  object TestContactEmailController extends ContactEmailController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val postSignInRedirectUrl = MockConfig.ggSignInContinueUrl
  }

  "The ContactEmailController controller" should {
    "use the correct applicationConfig" in {
      ContactEmailController.applicationConfig must be (FrontendAppConfig)
    }
    "use the correct authConnector" in {
      ContactEmailController.authConnector must be (FrontendAuthConnector)
    }
    "use the correct postSignInRedirectUrl" in {
      ContactEmailController.postSignInRedirectUrl must be (FrontendAppConfig.ggSignInContinueUrl)
    }
  }

  "Calling the showContactEmail action of the ContactEmailController with an authorised user" should {

    lazy val result = TestContactEmailController.showContactEmail(authenticatedFakeRequest())
    lazy val document = Jsoup.parse(contentAsString(result))


    "return status (200)" in {
      status(result) must be (Status.OK)
    }

    "render the Contact Email address view" in {
      document.title() mustBe Messages("contact_email.title")
    }
  }

  "Calling the submitContactEmail action of the ContactEmailController with an authorised user" should {

    lazy val result = TestContactEmailController.submitContactEmail(authenticatedFakeRequest())

    "return a redirect status (SEE_OTHER - 303)" in {
      status(result) must be (Status.SEE_OTHER)
    }

    s"redirect to '${controllers.routes.TermsController.showTerms().url}'" in {
      redirectLocation(result) mustBe Some(controllers.routes.TermsController.showTerms().url)
    }
  }

  authorisationTests
}