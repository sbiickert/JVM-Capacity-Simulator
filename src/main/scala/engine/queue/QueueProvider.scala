package ca.esri.capsim
package engine.queue

trait QueueProvider:
  def provideQueue(): MultiQueue
