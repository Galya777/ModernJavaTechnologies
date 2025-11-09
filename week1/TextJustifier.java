package modernJava.week1;

public class TextJustifier {
    public static String[] justifyText(String[] words, int maxWidth) {
        if (words == null || words.length == 0) {
            return new String[0];
        }

        // Първо изчисляваме колко реда ще имаме
        int lineCount = 0;
        int currentLineLength = 0;
        int wordCountInLine = 0;

        for (int i = 0; i < words.length; ) {
            if (wordCountInLine == 0) {
                currentLineLength = words[i].length();
                wordCountInLine = 1;
                i++;
            } else {
                int newLength = currentLineLength + 1 + words[i].length();
                if (newLength <= maxWidth) {
                    currentLineLength = newLength;
                    wordCountInLine++;
                    i++;
                } else {
                    lineCount++;
                    currentLineLength = 0;
                    wordCountInLine = 0;
                }
            }
        }
        if (wordCountInLine > 0) {
            lineCount++;
        }

        String[] result = new String[lineCount];
        int resultIndex = 0;
        int startWordIndex = 0;

        while (startWordIndex < words.length) {
            // Намираме колко думи могат да се поберат на текущия ред
            int endWordIndex = startWordIndex;
            int lineLength = words[endWordIndex].length();
            endWordIndex++;

            while (endWordIndex < words.length) {
                int nextLength = lineLength + 1 + words[endWordIndex].length();
                if (nextLength <= maxWidth) {
                    lineLength = nextLength;
                    endWordIndex++;
                } else {
                    break;
                }
            }

            int wordCount = endWordIndex - startWordIndex;
            StringBuilder line = new StringBuilder();

            if (wordCount == 1 || endWordIndex == words.length) {
                // Ляво подравняване за единични думи или последен ред
                line.append(words[startWordIndex]);
                for (int i = startWordIndex + 1; i < endWordIndex; i++) {
                    line.append(" ").append(words[i]);
                }
                // Добавяме интервали до края на реда
                while (line.length() < maxWidth) {
                    line.append(" ");
                }
            } else {
                // Двустранно подравняване
                int totalSpaces = maxWidth - lineLength;
                int baseSpaces = totalSpaces / (wordCount - 1) + 1;
                int extraSpaces = totalSpaces % (wordCount - 1);

                line.append(words[startWordIndex]);
                for (int i = startWordIndex + 1; i < endWordIndex; i++) {
                    int spaces = baseSpaces;
                    if (extraSpaces > 0) {
                        spaces++;
                        extraSpaces--;
                    }
                    for (int j = 0; j < spaces; j++) {
                        line.append(" ");
                    }
                    line.append(words[i]);
                }
            }

            result[resultIndex++] = line.toString();
            startWordIndex = endWordIndex;
        }

        return result;
    }
}

