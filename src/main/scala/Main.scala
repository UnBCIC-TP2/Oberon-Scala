import com.sun.jna._

//import com.github.nadavwr.ffi._

 
trait lib extends Library {
  def open(path:String, flag:Int):Int
  def close(fd:Int):Unit
  def lseek(fildes:Int, offset:Long, whence:Int):Long

  def ioctl(fd:Int, request:Int):Int
  def ioctl(fd:Int, request:Int, args:Array[_]):Int
  def ioctl(fd:Int, request:Int, ptr:Pointer):Int
  def malloc(size:Int):Pointer
  def calloc(nitems:Int, size:Int):Pointer
  def free(ptr:Pointer):Unit

  // nossas alterações
  def abs(n: Int): Int
  def powf(a: Float, b: Float): Float
  def max(a: Int, b: Int): Int
}

object interface {
  private var _lib:lib = null
  def run():lib = {
    if ( _lib == null ) {
      _lib = Native.loadLibrary("c++", classOf[lib]).asInstanceOf[lib]
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

object Main extends App {

/* libffi
  val libc = Module.open("libc.so") // "libc.so" for Linux
  val pow = libc.prepare[CInt, CInt, CInt]("pow")
  val num = stackalloc[CInt]; !num = 10
  val denom = stackalloc[CInt]; !denom = 4
  val result = stackalloc[Cint]
  pow(num, denom)(result) // last argument points to result
    
  println(s"pow(10, 4) = ${result})")
 end libffi */




  val ans1 = interface.run.abs(-100)
  println(ans1)

  val ans2 = interface.run.powf(2, -5)
  println(ans2)
  
  val d = interface.run.max(8, 4)
  println(d)
}

