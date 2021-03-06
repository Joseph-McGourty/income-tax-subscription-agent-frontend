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
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import utils.Implicits._

@Singleton
class ClientDetailsErrorController @Inject()(val baseConfig: BaseControllerConfig,
                                             val messagesApi: MessagesApi
                                            ) extends BaseController {

  lazy val show: Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      Ok(views.html.client_details_error(postAction = controllers.matching.routes.ClientDetailsErrorController.submit()))
  }

  lazy val submit: Action[AnyContent] = Authorised.async { implicit user =>
    implicit request =>
      Redirect(controllers.matching.routes.ClientDetailsController.show())
  }

}
