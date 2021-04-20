import com.sun.jna._

 
trait lib extends Library {

  def abs(n: Int): Int
  def div(numerator: Int, denominator: Int): Int
  def isalnum(c: Int): Int
  def isalpha(c: Int): Int
  def isascii(c: Int): Int
  def isblank(c: Int): Int 
  def islower(c: Int): Int
  def isprint(c: Int): Int 
  def ispunct(c: Int): Int 
  def isspace(c: Int): Int 
  def isupper(c: Int): Int
  def isxdigit(c: Int): Int 
  def rand(): Int
  def srand(seed: Int): Unit
}

object interface {
  private var _lib:lib = null
  def run():lib = {
    if ( _lib == null ) {
      _lib = Native.loadLibrary("c", classOf[lib]).asInstanceOf[lib]
    }
    _lib
  }

  def call:lib = run()

  val O_ACCMODE    =   3
  val O_RDONLY     =   0
  val O_WRONLY     =   1
  val O_RDWR       =   2
  val O_CREAT      = 100
  val IOCTL_TRIM   = 0x1277
  val SEEK_END     =   2
}

object External extends App {



  val ans1 = interface.run.abs(-100)
  println(ans1) 

  val ans2 = interface.run.div(34, 3)
  println(ans2)
  
  val ans3 = interface.run.isalnum('-')
  println(ans3)

  val ans4 = interface.run.isalpha('b')
  println(ans4)

  val ans5 = interface.run.isascii('&')
  println(ans5)

  val ans6 = interface.run.isblank(' ')
  println(ans6)

  val ans7 = interface.run.islower('A')
  println(ans7)

  val ans8 = interface.run.isprint('a')
  println(ans8)

  val ans9 = interface.run.ispunct('.')
  println(ans9)

  val ans10 = interface.run.isspace('-')
  println(ans10)

  val ans11 = interface.run.isupper('l')
  println(ans11)

  val ans12 = interface.run.isxdigit('0')
  println(ans12)

  interface.run.srand(3423)

  val ans13 = interface.run.rand()
  println(ans13)

 
 
}

