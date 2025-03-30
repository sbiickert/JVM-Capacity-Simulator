package ca.esri.capsim
package engine

trait Validatable:
  def isValid: Boolean = validate.isEmpty
  def validate: List[ValidationMessage]
