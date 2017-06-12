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
import forms._
import models.agent.ClientDetailsModel
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Request}
import play.twirl.api.Html
import services.KeystoreService

import scala.concurrent.Future

@Singleton
class ClientDetailsController @Inject()(val baseConfig: BaseControllerConfig,
                                        val messagesApi: MessagesApi,
                                        val keystoreService: KeystoreService
                                       ) extends BaseController {

  def view(clientDetailsForm: Form[ClientDetailsModel], isEditMode: Boolean)(implicit request: Request[_]): Html =
    views.html.client_details(
      clientDetailsForm,
      controllers.matching.routes.ClientDetailsController.submit(editMode = isEditMode),
      isEditMode
    )

  def show(isEditMode: Boolean): Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      keystoreService.fetchClientDetails() map {
        clientDetails => Ok(view(ClientDetailsForm.clientDetailsForm.form.fill(clientDetails), isEditMode = isEditMode))
      }
  }

  def submit(isEditMode: Boolean): Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      ClientDetailsForm.clientDetailsForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, isEditMode = isEditMode))),
        clientDetails => {
          val persist = keystoreService.fetchClientDetails().flatMap {
            case Some(oldDetails) if oldDetails == clientDetails =>
              Future.successful()
            case Some(_) =>
              // n.b. the delete must come before the save otherwise nothing will ever be saved.
              // this feature is currently NOT unit testable
              for {
                _ <- keystoreService.deleteAll()
                _ <- keystoreService.saveClientDetails(clientDetails)
              } yield Unit
            case None =>
              keystoreService.saveClientDetails(clientDetails)
          }

          for {
            _ <- persist
          } yield Redirect(routes.ConfirmClientController.show())
        }

      )
  }

}
