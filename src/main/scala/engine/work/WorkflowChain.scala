package ca.esri.capsim
package engine.work

import engine.compute.ServiceProvider
import engine.{Described, Validatable, ValidationMessage}

case class WorkflowChain(name: String, description: String,
                         steps: List[WorkflowDefStep], serviceProviders: Set[ServiceProvider])
  extends Described, Validatable:
  def allRequiredServiceTypes: Set[String] =
    steps.map(_.serviceType).toSet

  def missingServiceProviders: List[String] =
    val allRequired = allRequiredServiceTypes
    val configured = serviceProviders.map(_.service.serviceType).toSet
    (allRequired -- configured).toList

  private def hasDuplicateServiceProviders: Boolean =
    val configured = serviceProviders.map(_.service.serviceType).toSet
    configured.size != serviceProviders.size // Duplicate service types will end up with a set smaller than

  override def validate: List[ValidationMessage] =
    var eList = List[ValidationMessage]()
    if hasDuplicateServiceProviders then
      eList = ValidationMessage("More than one service provider for a type", name) +: eList
    if missingServiceProviders.nonEmpty then
      eList = ValidationMessage("Missing one or more service providers", name) +: eList
    eList
    
  def serviceProvider(index:Int): ServiceProvider =
    assert(index >= 0 && index < steps.size)
    serviceProviders.find(_.service.serviceType == steps(index).serviceType).get
    
  def serviceProvider(step: WorkflowDefStep): ServiceProvider =
    serviceProviders.find(_.service.serviceType == step.serviceType).get