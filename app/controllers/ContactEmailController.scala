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

import javax.inject.Inject

import config.BaseControllerConfig
import forms.{EmailForm, IncomeSourceForm, PropertyIncomeForm}
import models.EmailModel
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Request}
import play.twirl.api.Html
import services.KeystoreService

import scala.concurrent.Future

class ContactEmailController @Inject()(val baseConfig: BaseControllerConfig,
                                       val messagesApi: MessagesApi,
                                       val keystoreService: KeystoreService
                                      ) extends BaseController {

  def view(contactEmailForm: Form[EmailModel], backUrl: String, isEditMode: Boolean)(implicit request: Request[_]): Html =
    views.html.contact_email(
      contactEmailForm = contactEmailForm,
      postAction = controllers.routes.ContactEmailController.submitContactEmail(editMode = isEditMode),
      backUrl = backUrl
    )

  def showContactEmail(isEditMode: Boolean): Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      for {
        contactEmail <- keystoreService.fetchContactEmail()
        backUrl <- backUrl
      } yield Ok(view(contactEmailForm = EmailForm.emailForm.fill(contactEmail), backUrl = backUrl, isEditMode = isEditMode))
  }

  def submitContactEmail(isEditMode: Boolean): Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      EmailForm.emailForm.bindFromRequest.fold(
        formWithErrors => backUrl.map(backUrl => BadRequest(view(contactEmailForm = formWithErrors, backUrl = backUrl, isEditMode = isEditMode))),
        contactEmail => {
          keystoreService.saveContactEmail(contactEmail) map (_ =>
            if (isEditMode)
              Redirect(controllers.routes.SummaryController.showSummary())
            else
              Redirect(controllers.routes.TermsController.showTerms()))
        }
      )
  }

  def backUrl(implicit request: Request[_]): Future[String] =
    keystoreService.fetchIncomeSource() flatMap {
      case Some(source) => source.source match {
        case IncomeSourceForm.option_business | IncomeSourceForm.option_both =>
          Future.successful(controllers.business.routes.BusinessIncomeTypeController.showBusinessIncomeType().url)
        case _ =>
          keystoreService.fetchPropertyIncome() map {
            case Some(propertyIncome) => propertyIncome.incomeValue match {
              case PropertyIncomeForm.option_LT10k =>
                controllers.routes.NotEligibleController.showNotEligible().url
              case PropertyIncomeForm.option_GE10k =>
                controllers.routes.EligibleController.showEligible().url
            }
          }
      }
    }

}