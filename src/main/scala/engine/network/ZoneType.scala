package ca.esri.capsim
package engine.network

enum ZoneType(zt:String):
  case INTERNET extends ZoneType("INTERNET")
  case EDGE extends ZoneType("EDGE")
  case SECURED extends ZoneType("SECURED")