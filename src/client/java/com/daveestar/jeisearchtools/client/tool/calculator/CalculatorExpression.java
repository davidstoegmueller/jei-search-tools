package com.daveestar.jeisearchtools.client.tool.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

public final class CalculatorExpression {
  private static final MathContext PRECISION = MathContext.DECIMAL128;
  private static final MathContext DISPLAY_PRECISION = new MathContext(12);
  private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
  private static final BigDecimal FULL_TURN = BigDecimal.valueOf(360);

  private final String input;
  private int position;
  private int depth;
  private boolean calculation;

  private CalculatorExpression(String input) {
    this.input = input;
  }

  public static Optional<String> evaluate(String input) {
    if (input.isBlank() || input.length() > 256) {
      return Optional.empty();
    }

    try {
      CalculatorExpression parser = new CalculatorExpression(input);
      BigDecimal value = parser.expression();
      parser.skipWhitespace();

      if (parser.position != input.length() || !parser.calculation) {
        return Optional.empty();
      }

      BigDecimal rounded = value.round(DISPLAY_PRECISION).stripTrailingZeros();
      int plainLength = rounded.precision() + Math.max(0, -rounded.scale())
          + Math.max(0, rounded.scale() - rounded.precision()) + 2;

      return Optional.of(plainLength <= 24 ? rounded.toPlainString() : rounded.toString());
    } catch (IllegalArgumentException | ArithmeticException ignored) {
      return Optional.empty();
    }
  }

  private BigDecimal expression() {
    BigDecimal value = term();

    while (true) {
      if (take('+')) {
        calculation = true;
        value = value.add(term(), PRECISION);
      } else if (take('-') || take('−')) {
        calculation = true;
        value = value.subtract(term(), PRECISION);
      } else {
        return value;
      }
    }
  }

  private BigDecimal term() {
    BigDecimal value = unary();

    while (true) {
      if (take('*') || take('×')) {
        calculation = true;
        value = value.multiply(unary(), PRECISION);
      } else if (take('/') || take('÷')) {
        calculation = true;
        value = value.divide(unary(), PRECISION);
      } else {
        return value;
      }
    }
  }

  private BigDecimal unary() {
    if (++depth > 64) {
      throw new IllegalArgumentException("Expression is too deeply nested");
    }

    try {
      if (take('+')) {
        calculation = true;
        return unary();
      }

      if (take('-') || take('−')) {
        calculation = true;
        return unary().negate();
      }

      BigDecimal value = primary();
      if (take('%')) {
        calculation = true;
        value = value.divide(HUNDRED, PRECISION);
      }

      if (take('^')) {
        calculation = true;
        BigDecimal exponent = unary();

        if (exponent.stripTrailingZeros().scale() <= 0 && exponent.abs().compareTo(BigDecimal.valueOf(10000)) <= 0) {
          value = value.pow(exponent.intValueExact(), PRECISION);
        } else {
          value = finite(Math.pow(value.doubleValue(), exponent.doubleValue()));
        }
      }

      return value;
    } finally {
      depth--;
    }
  }

  private BigDecimal primary() {
    if (take('(')) {
      BigDecimal value = expression();
      require(')');
      return value;
    }

    skipWhitespace();

    int start = position;
    while (position < input.length() && Character.isLetter(input.charAt(position))) {
      position++;
    }

    if (position != start) {
      String name = input.substring(start, position).toLowerCase(java.util.Locale.ROOT);

      if (name.equals("pi")) {
        return BigDecimal.valueOf(Math.PI);
      }

      require('(');
      BigDecimal argument = expression();
      require(')');

      calculation = true;

      return switch (name) {
        case "sqrt" -> argument.sqrt(PRECISION);
        case "sin" -> trigonometry(argument, true);
        case "cos" -> trigonometry(argument, false);
        default -> throw new IllegalArgumentException("Unknown function");
      };
    }

    return number();
  }

  private BigDecimal number() {
    int start = position;
    while (position < input.length() && isDigit(input.charAt(position))) {
      position++;
    }

    if (position < input.length() && (input.charAt(position) == '.' || input.charAt(position) == ',')) {
      position++;
      int fractionStart = position;

      while (position < input.length() && isDigit(input.charAt(position))) {
        position++;
      }

      if (position == fractionStart) {
        throw new IllegalArgumentException("Incomplete decimal");
      }
    }

    if (position < input.length() && (input.charAt(position) == 'e' || input.charAt(position) == 'E')) {
      position++;

      if (position < input.length() && (input.charAt(position) == '+' || input.charAt(position) == '-')) {
        position++;
      }

      int exponentStart = position;
      while (position < input.length() && isDigit(input.charAt(position))) {
        position++;
      }

      if (position == exponentStart) {
        throw new IllegalArgumentException("Incomplete exponent");
      }
    }
    BigDecimal value = new BigDecimal(input.substring(start, position).replace(',', '.'), PRECISION);
    if (Math.abs((long) value.scale()) > 10000) {
      throw new IllegalArgumentException("Number is too large");
    }

    return value;
  }

  private static boolean isDigit(char character) {
    return character >= '0' && character <= '9';
  }

  private static BigDecimal trigonometry(BigDecimal degrees, boolean sine) {
    double angle = degrees.remainder(FULL_TURN).doubleValue();
    if (angle % 90 == 0) {
      int quadrant = Math.floorMod((int) (angle / 90), 4);

      return BigDecimal.valueOf(sine
          ? switch (quadrant) {
            case 1 -> 1;
            case 3 -> -1;
            default -> 0;
          }
          : switch (quadrant) {
            case 0 -> 1;
            case 2 -> -1;
            default -> 0;
          });
    }

    double radians = Math.toRadians(angle);
    return finite(sine ? Math.sin(radians) : Math.cos(radians));
  }

  private static BigDecimal finite(double value) {
    if (!Double.isFinite(value)) {
      throw new ArithmeticException("Undefined result");
    }

    return BigDecimal.valueOf(value);
  }

  private boolean take(char expected) {
    skipWhitespace();

    if (position < input.length() && input.charAt(position) == expected) {
      position++;
      return true;
    }

    return false;
  }

  private void require(char expected) {
    if (!take(expected)) {
      throw new IllegalArgumentException("Missing delimiter");
    }
  }

  private void skipWhitespace() {
    while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
      position++;
    }
  }
}
