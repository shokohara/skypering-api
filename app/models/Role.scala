package models

abstract sealed trait Role extends DBValue[Int] {
  val DB_VALUE: Int
}

object Role {
  def toRole(int: Int): Role = int match {
    case x if x == Administrator.DB_VALUE => Administrator
    case x if x == NormalUser.DB_VALUE => NormalUser
  }
}

case object Administrator extends Role {
  val DB_VALUE = 0
}

case object NormalUser extends Role {
  val DB_VALUE = 1
}
