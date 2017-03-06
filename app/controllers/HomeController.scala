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
import connectors.models.throttling.CanAccess
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.ThrottlingService
import uk.gov.hmrc.play.http.InternalServerException
import utils.Implicits._

class HomeController @Inject()(override val baseConfig: BaseControllerConfig,
                               override val messagesApi: MessagesApi,
                               throttlingService: ThrottlingService
                              ) extends BaseController {

  def index: Action[AnyContent] = Authorised.asyncForHomeController { implicit user =>
    implicit request =>
      throttlingService.checkAccess.flatMap {
        case Some(CanAccess) =>
          Redirect(controllers.preferences.routes.PreferencesController.checkPreferences())
        case Some(_) =>
          // TODO show the page
          Ok("exceeds limit")
        case _ => new InternalServerException("HomeController.index: unexpected error calling the throttling service")
      }
  }

}
