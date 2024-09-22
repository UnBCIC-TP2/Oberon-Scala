
---

# Bitwise Operations in Oberon Interpreter (Scala)

## Overview

This project adds **bitwise operations** support to an **Oberon interpreter** implemented in Scala. The implementation introduces bitwise operators such as AND (`&`), OR (`|`), XOR (`^`), NOT (`~`), Left Shift (`<<`), and Right Shift (`>>`) for evaluating bitwise expressions.

The parser reads a list of tokens representing the bitwise expressions, builds the corresponding Abstract Syntax Tree (AST), and evaluates the result.

## Features

- **Bitwise Expressions:** Support for operations like AND, OR, XOR, NOT, Left Shift, and Right Shift.
- **Parser:** Parses bitwise expressions from tokens and builds the corresponding AST.
- **Interpreter:** Evaluates bitwise expressions to produce integer results.
- **Tests:** Includes unit tests for each bitwise operation.

## Bitwise Operators Supported

| Operator  | Symbol | Description                |
|-----------|--------|----------------------------|
| AND       | `&`    | Bitwise AND                |
| OR        | `|`    | Bitwise OR                 |
| XOR       | `^`    | Bitwise XOR                |
| NOT       | `~`    | Bitwise NOT (negation)      |
| Left Shift| `<<`   | Bitwise Left Shift          |
| Right Shift| `>>`  | Bitwise Right Shift         |

## Getting Started

To use the implementation, you can clone this repository and run the tests. Make sure you have **Scala** and **SBT** (Scala Build Tool) installed.

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/oberon-bitwise-operations.git

# Navigate to the project directory
cd oberon-bitwise-operations

# Compile the project
sbt compile

# Run tests
sbt test
```

## Usage

You can evaluate bitwise expressions in Scala using the implemented **interpreter**. Below are some use cases and examples:

### Evaluating Expressions

To evaluate expressions, the interpreter uses an abstract class `BitwiseExpression`. Here are some examples of how different bitwise operations are evaluated:

### Example 1: Bitwise AND

```scala
val expr = BitwiseAnd(BitwiseIntValue(12), BitwiseIntValue(5))
val result = interpreter.evaluate(expr) // result = 12 & 5 = 4
```

### Example 2: Bitwise OR

```scala
val expr = BitwiseOr(BitwiseIntValue(12), BitwiseIntValue(5))
val result = interpreter.evaluate(expr) // result = 12 | 5 = 13
```

### Example 3: Bitwise XOR

```scala
val expr = BitwiseXor(BitwiseIntValue(12), BitwiseIntValue(5))
val result = interpreter.evaluate(expr) // result = 12 ^ 5 = 9
```

### Example 4: Left Shift

```scala
val expr = LeftShift(BitwiseIntValue(4), BitwiseIntValue(2))
val result = interpreter.evaluate(expr) // result = 4 << 2 = 16
```

### Example 5: Right Shift

```scala
val expr = RightShift(BitwiseIntValue(16), BitwiseIntValue(2))
val result = interpreter.evaluate(expr) // result = 16 >> 2 = 4
```

### Example 6: Bitwise NOT

```scala
val expr = BitwiseNot(BitwiseIntValue(12))
val result = interpreter.evaluate(expr) // result = ~12 = -13
```

## Token Representation

The **parser** works by reading a list of tokens that represent bitwise expressions. These tokens are:

- `NumberToken`: Represents a numerical value (integer).
- `VariableToken`: Represents a variable name (future enhancement).
- `BitwiseAndToken`: Represents the `&` operator.
- `BitwiseOrToken`: Represents the `|` operator.
- `BitwiseXorToken`: Represents the `^` operator.
- `LeftShiftToken`: Represents the `<<` operator.
- `RightShiftToken`: Represents the `>>` operator.
- `BitwiseNotToken`: Represents the `~` operator.

The parser will consume these tokens and build the corresponding AST, which is then evaluated.

## Running the Tests

The project includes unit tests to verify the correctness of the implementation. The tests cover all bitwise operators and their expected outcomes.

To run the tests, use the following command:

```bash
sbt test
```

Example of a test case for Bitwise AND:

```scala
test("BitwiseAnd should evaluate correctly") {
  val expr = BitwiseAnd(BitwiseIntValue(12), BitwiseIntValue(5))
  assert(interpreter.evaluate(expr) === (12 & 5)) // Expect 4
}
```

## Future Enhancements

- Add support for variables and assignment in bitwise expressions.
- Extend the parser to handle more complex expressions, such as chained bitwise operations and parentheses.

## Contributing

Feel free to fork the project, create a feature branch, and submit a pull request. Contributions are welcome!

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/my-feature`).
3. Commit your changes (`git commit -am 'Add my feature'`).
4. Push to the branch (`git push origin feature/my-feature`).
5. Create a new Pull Request.
