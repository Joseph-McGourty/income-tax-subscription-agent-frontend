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

import javax.inject.{Inject, Singleton}

import config.BaseControllerConfig
import models.agent.ClientDetailsModel
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.{ClientRelationshipService, KeystoreService}

import scala.concurrent.Future

@Singleton
class ClientRelationshipController @Inject()(val baseConfig: BaseControllerConfig,
                                             val messagesApi: MessagesApi,
                                             clientRelationshipService: ClientRelationshipService,
                                             keystoreService: KeystoreService) extends BaseController {
  val checkClientRelationship: Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      (
        for {
          clientNINO <- keystoreService.fetchClientDetails()
            .collect { case Some(ClientDetailsModel(_, _, nino, _)) => nino }
          isPreExistingRelationship <- clientRelationshipService.isPreExistingRelationship(clientNINO)
        } yield if (isPreExistingRelationship) {
            Redirect(controllers.routes.IncomeSourceController.showIncomeSource())
          }
          else Redirect(controllers.routes.ClientRelationshipController.noClientError())
        ).recover { case _ => InternalServerError }
  }

  val noClientError: Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      Future.successful(Ok(views.html.no_client_relationship(postAction = controllers.routes.ClientRelationshipController.noClientErrorRedirect())))
  }

  val noClientErrorRedirect: Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      Future.successful(Redirect(controllers.matching.routes.ClientDetailsController.show()))
  }
}
