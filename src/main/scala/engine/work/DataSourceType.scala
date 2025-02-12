package ca.esri.capsim
package engine.work

enum DataSourceType(dst:String):
  case RELATIONAL extends DataSourceType("RELATIONAL")
  case OBJECT extends DataSourceType("OBJECT")
  case DBMS extends DataSourceType("DBMS")
  case FILE extends DataSourceType("FILE")
  case BIG extends DataSourceType("BIG")
  case OTHER extends DataSourceType("OTHER")
  case NONE extends DataSourceType("NONE")

end DataSourceType

object DataSourceType:
  def fromString(dst:String): DataSourceType =
    dst match
      case "RELATIONAL" => RELATIONAL
      case "OBJECT" => OBJECT
      case "DBMS" => DBMS
      case "FILE" => FILE
      case "BIG" => BIG
      case "OTHER" => OTHER
      case _ => NONE
end DataSourceType
