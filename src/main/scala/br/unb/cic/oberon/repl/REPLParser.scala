package br.unb.cic.oberon.repl

import org.jline.reader.Parser.ParseContext
import org.jline.reader.{CompletingParsedLine, EOFError, ParsedLine, Parser}

import scala.collection.mutable.{ListBuffer, StringBuilder}
import scala.jdk.CollectionConverters._

class REPLParser extends Parser {
  override def parse(
      line: String,
      cursor: Int,
      context: Parser.ParseContext
  ): ParsedLine = {
    val words = new ListBuffer[String]
    val current = new StringBuilder
    var wordCursor = -1
    var wordIndex = -1
    var rawWordCursor = -1
    var rawWordLength = -1
    var rawWordStart = 0

    for (i <- 0 until line.length) {
      if (i == cursor) {
        wordIndex = words.size
        wordCursor = current.length
        rawWordCursor = i - rawWordStart
      }

      if (isDelimiter(line, i)) {
        rawWordLength = handleDelimiterAndGetRawWordLength(
          current,
          words,
          rawWordStart,
          rawWordCursor,
          rawWordLength,
          i
        )
        rawWordStart = i + 1
      } else {
        if (!isEscapeChar(line, i) || context == ParseContext.SPLIT_LINE) {
          current += line.charAt(i)
        }
      }
    }

    if (current.nonEmpty || (cursor == line.length)) {
      words += current.toString
      if (rawWordCursor >= 0 && rawWordLength < 0)
        rawWordLength = line.length - rawWordStart
    }

    if (cursor == line.length) {
      wordIndex = words.size - 1
      wordCursor = words.last.length
      rawWordCursor = cursor - rawWordStart
      rawWordLength = rawWordCursor
    }

    if (
      (context != ParseContext.COMPLETE) && (context != ParseContext.SPLIT_LINE)
    ) {
      /*
        Use of "\" prototype, not working, can't change REPLParser line
       if (isEscapeChar(line, line.length() - 1)) {
        throw new EOFError(-1, -1, "Escaped new line", "newline");
      } */
      if (isThen(line.substring(line.lastIndexOf(" ") + 1))) {
        throw new EOFError(-1, -1, "THEN new line");
      }
      if (isElse(line.substring(line.lastIndexOf(" ") + 1))) {
        throw new EOFError(-1, -1, "Else new line");
      }
      if (isElseIf(line.substring(line.lastIndexOf(" ") + 1))) {
        throw new EOFError(-1, -1, "ELSEIF new line");
      }
      if (isCase(line.substring(line.lastIndexOf(" ") + 1))) {
        throw new EOFError(-1, -1, "CASE new line");
      }
      if (isOf(line.substring(line.lastIndexOf(" ") + 1))) {
        throw new EOFError(-1, -1, "Case OF new line");
      }
      if (isRepeat(line.substring(line.lastIndexOf(" ") + 1))) {
        throw new EOFError(-1, -1, "REPEAT new line");
      }
      /* Not usefull enough
      if(isUntil(line.substring(line.lastIndexOf(" ") + 1))){
        throw new EOFError(-1, -1, "UNTIL new line");
      } */
    }

    new ArgumentList(
      line,
      words.toList,
      wordIndex,
      wordCursor,
      cursor,
      rawWordCursor,
      rawWordLength
    )
  }

  private def handleDelimiterAndGetRawWordLength(
      current: StringBuilder,
      words: ListBuffer[String],
      rawWordStart: Int,
      rawWordCursor: Int,
      rawWordLength: Int,
      pos: Int
  ): Int = {
    if (current.nonEmpty) {
      words += current.toString
      current.setLength(0) // reset the arg
      if (rawWordCursor >= 0 && rawWordLength < 0) return pos - rawWordStart
    }
    rawWordLength
  }

  def isDelimiter(buffer: CharSequence, pos: Int): Boolean =
    Character.isWhitespace(buffer.charAt(pos))
  def isEscapeChar(buffer: CharSequence, pos: Int): Boolean = {
    if (pos < 0) false
    else isEscapeChar(buffer.charAt(pos))
  }
  def isThen(last: String): Boolean = last == "THEN"
  def isElse(last: String): Boolean = last == "ELSE"
  def isElseIf(last: String): Boolean = last == "ELSEIF"
  def isCase(last: String): Boolean = last == "CASE"
  def isOf(last: String): Boolean = last == "OF"
  def isRepeat(last: String): Boolean = last == "REPEAT"
  def isUntil(last: String): Boolean = last == "UNTIL"

  class ArgumentList(
      line: String,
      words: List[String],
      wordIndex: Int,
      wordCursor: Int,
      cursor: Int,
      rawWordCursor: Int,
      rawWordLength: Int
  ) extends ParsedLine
      with CompletingParsedLine {
    override def word(): String = words(wordIndex)
    override def wordCursor(): Int = wordCursor
    override def wordIndex(): Int = wordIndex
    override def words(): java.util.List[String] = words.asJava
    override def line(): String = line
    override def cursor(): Int = cursor
    override def rawWordCursor(): Int = rawWordCursor
    override def rawWordLength(): Int = rawWordLength

    override def escape(
        candidate: CharSequence,
        complete: Boolean
    ): CharSequence = {
      candidate
    }
  }

}
