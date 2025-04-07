package ca.esri.capsim
package app.io

import engine.work.{WorkflowChain, WorkflowDef, WorkflowDefStep}

import java.time.Instant


case class WorkflowDefLib(date: Instant,
                     baselineSpecPerCore: Double, 
                     steps: Map[String, WorkflowDefStep],
                     chains: Map[String, WorkflowChain],
                     workflows: Map[String, WorkflowDef]):

end WorkflowDefLib

object WorkflowDefLib:
  def empty: WorkflowDefLib =
    WorkflowDefLib(
      date = Instant.now, baselineSpecPerCore = -1.0, steps = Map.empty, chains = Map.empty, workflows = Map.empty
    )
end WorkflowDefLib

