package ca.esri.capsim
package engine.work

import engine.compute.ServiceProvider

case class WorkflowChain(steps: List[WorkflowDefStep], serviceProviders: Set[ServiceProvider]):
  def allRequiredServiceTypes: Set[String] =
    steps.map(_.serviceType).toSet

  def missingServiceProviders: List[String] =
    val allRequired = allRequiredServiceTypes
    val configured = serviceProviders.map(_.service.serviceType).toSet
    (allRequired -- configured).toList

  def hasDuplicateServiceProviders: Boolean =
    val configured = serviceProviders.map(_.service.serviceType).toSet
    configured.size != serviceProviders.size // Duplicate service types will end up with a set smaller than

  def isValid: Boolean =
    val noDupes = !hasDuplicateServiceProviders 
    val noMissing = missingServiceProviders.isEmpty
    noDupes && noMissing
    
  def serviceProvider(index:Int): ServiceProvider =
    assert(index >= 0 && index < steps.size)
    serviceProviders.find(_.service.serviceType == steps(index).serviceType).get
    
  def serviceProvider(step: WorkflowDefStep): ServiceProvider =
    serviceProviders.find(_.service.serviceType == step.serviceType).get