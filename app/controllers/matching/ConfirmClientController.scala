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

package controllers.matching

import javax.inject.{Inject, Singleton}

import config.BaseControllerConfig
import controllers.BaseController
import models.agent.ClientDetailsModel
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Request}
import play.twirl.api.Html
import services.{ClientMatchingService, KeystoreService}
import utils.Implicits._

@Singleton
class ConfirmClientController @Inject()(val baseConfig: BaseControllerConfig,
                                        val messagesApi: MessagesApi,
                                        val keystoreService: KeystoreService,
                                        val clientMatchingService: ClientMatchingService
                                       ) extends BaseController {

  def view(clientDetailsModel: ClientDetailsModel)(implicit request: Request[_]): Html =
    views.html.check_your_client_details(
      clientDetailsModel,
      routes.ConfirmClientController.submit(),
      backUrl
    )

  def show(): Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      keystoreService.fetchClientDetails() map {
        case Some(clientDetails) => Ok(view(clientDetails))
        case _ => Redirect(routes.ClientDetailsController.showClientDetails())
      }
  }

  def submit(): Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      keystoreService.fetchClientDetails() flatMap {
        case Some(clientDetails) =>
          clientMatchingService.matchClient(clientDetails) map {
            case true => Redirect(controllers.routes.IncomeSourceController.showIncomeSource())
            case false => Redirect(controllers.matching.routes.ClientDetailsErrorController.show())
          }
        case _ => Redirect(routes.ClientDetailsController.showClientDetails())
      }
  }

  lazy val backUrl: String = routes.ClientDetailsController.showClientDetails().url

}
