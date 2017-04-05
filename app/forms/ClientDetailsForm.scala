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

package forms

import forms.prevalidation.PreprocessedForm
import forms.submapping.DateMapping.dateMapping
import forms.validation.utils.ConstraintUtil._
import forms.validation.utils.MappingUtil._
import models.ClientDetailsModel
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraint
import validation.Constraints._

object ClientDetailsForm {

  val clientFirstName = "clientFirstName"
  val clientLastName = "clientLastName"
  val clientNino = "clientNino"
  val clientDateOfBirth = "clientDateOfBirth"

  val nameMaxLength = 105

  val firstNameNonEmpty: Constraint[String] = nonEmpty("error.client_details.first_name.empty")
  val lastNameNonEmpty: Constraint[String] = nonEmpty("error.client_details.last_name.empty")

  val firstNameInvalid: Constraint[String] = invalidFormat("error.client_details.first_name.invalid")
  val lastNameInvalid: Constraint[String] = invalidFormat("error.client_details.last_name.invalid")

  val firstNameMaxLength: Constraint[String] = maxLength(nameMaxLength, "error.client_details.first_name.maxLength")
  val lastNameMaxLength: Constraint[String] = maxLength(nameMaxLength, "error.client_details.last_name.maxLength")

  val clientDetailsValidationForm = Form(
    mapping(
      clientFirstName -> oText.toText.verifying(firstNameNonEmpty andThen firstNameMaxLength andThen firstNameInvalid),
      clientLastName -> oText.toText.verifying(lastNameNonEmpty andThen lastNameMaxLength andThen lastNameInvalid),
      clientNino -> oText.toText.verifying(emptyNino andThen validateNino),
      clientDateOfBirth -> dateMapping.verifying(dateEmpty andThen dateValidation)
    )(ClientDetailsModel.apply)(ClientDetailsModel.unapply)
  )

  val clientDetailsForm = PreprocessedForm(clientDetailsValidationForm)

}
