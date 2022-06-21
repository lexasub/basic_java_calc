import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Please, enter basic expression. ex: x op y, where op=[+-*/], x,y is number arabic or roman once");
        Scanner keyboard = new Scanner(System.in);
        System.out.println(calc(keyboard.nextLine()));
    }
    enum RomanNumeral {
        I(1), IV(4), V(5), IX(9), X(10),
        XL(40), L(50), XC(90), C(100),
        CD(400), D(500), CM(900), M(1000);
        private final int value;
        RomanNumeral(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        public static List<RomanNumeral> getReverseSortedValues() {
            return Arrays.stream(values())
                    .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                    .collect(Collectors.toList());
        }
    }
    static int romanToArabic(String input) {
        String romanNumeral = input.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            throw new IllegalArgumentException(input + " cannot be converted to a Roman Numeral");
        }

        return result;
    }
    static String arabicToRoman(int number) {
        if ((number <= 0) || (number > 4000)) {
            throw new IllegalArgumentException(number + " is not in range (0,4000]");
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }
    static boolean getType(String arg) {
        if(arg.matches("-?\\d+")) return true;
        if(arg.matches("([IVX])+")) return false;
        throw new IllegalArgumentException ("number is wrong");
    }
    static String do_op(String arg0, String arg1, IntBinaryOperator op) {
        boolean type0 = getType(arg0);
        boolean type1 = getType(arg1);
        if(type0 != type1) throw new IllegalArgumentException ("types is unsuitable");
        int iarg0 = (type0)? Integer.parseInt(arg0) : romanToArabic(arg0);
        int iarg1 = (type0)? Integer.parseInt(arg1) : romanToArabic(arg1);
        if (iarg0 < 1 || iarg0 > 10 || iarg1 < 1 || iarg1 > 10) throw new IllegalArgumentException("some arguments <1 or >10");
        if(type0) return String.valueOf(op.applyAsInt(iarg0,iarg1));
        return arabicToRoman(op.applyAsInt(iarg0,iarg1));
    }

    public static String calc(String input) {
        String ops = "[+\\-*/]";
        String[] args = Arrays.stream(input.split(ops)).map(String::trim).toArray(String[]::new);
        Pattern p = Pattern.compile(ops);
        Matcher m = p.matcher(input);
        m.find();
        String op = m.group();
        m.find();
        if(!m.hitEnd() || args.length != 2 ) throw new IllegalArgumentException("use 2 args and 1 op");
        return switch (op) {
            case "+" -> do_op(args[0], args[1], (a, b) -> a + b);
            case "-" -> do_op(args[0], args[1], (a, b) -> a - b);
            case "*" -> do_op(args[0], args[1], (a, b) -> a * b);
            case "/" -> do_op(args[0], args[1], (a, b) -> a / b);
            default -> throw new IllegalArgumentException("operation is wrong");
        };
    }

}