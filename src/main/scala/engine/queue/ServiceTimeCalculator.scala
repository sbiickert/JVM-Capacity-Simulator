package ca.esri.capsim
package engine.queue

import engine.work.ClientRequest

trait ServiceTimeCalculator:
  def calculateServiceTime(request: ClientRequest): Int
  def calculateLatency(request: ClientRequest): Int
