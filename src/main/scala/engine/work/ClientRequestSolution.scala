package ca.esri.capsim
package engine.work

case class ClientRequestSolution(steps: List[ClientRequestSolutionStep]):
  def currentStep: ClientRequestSolutionStep = steps.head
  def gotoNextStep: ClientRequestSolution =
    this.copy(steps = steps.tail)

end ClientRequestSolution

