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

import config.{FrontendAppConfig, FrontendAuthConnector}
import forms.EmailForm
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import services.KeystoreService

import scala.concurrent.Future

object ContactEmailController extends ContactEmailController {
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val postSignInRedirectUrl = FrontendAppConfig.ggSignInContinueUrl
  override val keystoreService = KeystoreService
}

trait ContactEmailController extends BaseController {

  val keystoreService: KeystoreService

  val showContactEmail = Authorised.async { implicit user =>
    implicit request =>
      keystoreService.fetchContactEmail() map {
        contactEmail =>
          Ok(views.html.contact_email(
            contactEmailForm = EmailForm.emailForm.fill(contactEmail),
            postAction = controllers.routes.ContactEmailController.submitContactEmail()
          ))
      }
  }

  val submitContactEmail = Authorised.async { implicit user =>
    implicit request =>
      EmailForm.emailForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(NotImplemented)
        },
        contactEmail => {
          keystoreService.saveContactEmail(contactEmail) map (
            _ => Redirect(controllers.routes.TermsController.showTerms()))
        }
      )
  }
}