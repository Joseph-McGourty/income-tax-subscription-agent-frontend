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

package testonly.controllers

import javax.inject.{Inject, Singleton}

import config.BaseControllerConfig
import controllers.BaseController
import forms.ClientDetailsForm
import models.agent.ClientDetailsModel
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, Request}
import play.twirl.api.Html
import testonly.connectors.{MatchingStubConnector, UserData}
import testonly.forms.ClientToStubForm
import testonly.models.ClientToStubModel
import utils.Implicits._

//$COVERAGE-OFF$Disabling scoverage on this class as it is only intended to be used by the test only controller

@Singleton
class MatchingStubController @Inject()(override val baseConfig: BaseControllerConfig,
                                       override val messagesApi: MessagesApi,
                                       matchingStubConnector: MatchingStubConnector
                                      ) extends BaseController {

  def view(clientToStubForm: Form[ClientToStubModel])(implicit request: Request[_]): Html =
    testonly.views.html.stub_client(
      clientToStubForm,
      routes.MatchingStubController.stubClient()
    )


  def show = Action.async { implicit request =>
    Ok(view(ClientToStubForm.clientToStubForm.form.fill(UserData().toClientToStubModel)))
  }

  def stubClient = Action.async { implicit request =>
    ClientToStubForm.clientToStubForm.bindFromRequest.fold(
      formWithErrors => BadRequest(view(formWithErrors)),
      clientDetails =>
        matchingStubConnector.newUser(clientDetails) map {
          case true => Ok(testonly.views.html.show_stubbed_details(clientDetails))
          case _ => InternalServerError("calls to matching-stub failed")
        }
    )
  }

}

// $COVERAGE-ON$
