package ca.esri.capsim
package engine.compute

enum BalancingModel(t:String):
  case SINGLE extends BalancingModel("1")
  case ROUNDROBIN extends BalancingModel("ROUNDROBIN")
  case FAILOVER extends BalancingModel("FAILOVER")
  