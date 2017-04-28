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
import connectors.models.subscription.FESuccessResponse
import controllers.BaseController
import models.agent.ClientDetailsModel
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Request, Result}
import play.twirl.api.Html
import services.{ClientMatchingService, KeystoreService, SubscriptionService}
import utils.Implicits._

import scala.concurrent.Future

@Singleton
class ConfirmClientController @Inject()(val baseConfig: BaseControllerConfig,
                                        val messagesApi: MessagesApi,
                                        val keystoreService: KeystoreService,
                                        val clientMatchingService: ClientMatchingService,
                                        val subscriptionService: SubscriptionService
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
        case _ => Redirect(routes.ClientDetailsController.show())
      }
  }

  def submit(): Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      keystoreService.fetchClientDetails() flatMap {
        case Some(clientDetails) => {
          for {
            matchFound <- clientMatchingService.matchClient(clientDetails)
          } yield matchFound
        }.flatMap {
          case true => checkExistingSubscription(clientDetails, Redirect(controllers.routes.IncomeSourceController.showIncomeSource()))
          case false => Redirect(controllers.matching.routes.ClientDetailsErrorController.show())
        }
        // if there are no client details redirect them back to client details
        case _ => Redirect(routes.ClientDetailsController.show())
      }
  }

  lazy val backUrl: String = routes.ClientDetailsController.show().url


  private[matching]
  def checkExistingSubscription(clientDetails: ClientDetailsModel,
                                default: => Future[Result])(implicit request: Request[AnyContent]): Future[Result] =
    subscriptionService.getSubscription(clientDetails.ninoInBackendFormat).flatMap {
      case Some(FESuccessResponse(None)) => default
      case Some(FESuccessResponse(Some(_))) => Redirect(controllers.routes.ClientAlreadySubscribedController.show())
      case _ => showInternalServerError
    }

}
