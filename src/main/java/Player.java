
import java.util.*;

/**
 *
 * @author Prasad
 */
class Player {

    String name;
    private int score = 0;
    private List<String> wordsFound;

    Player() {
        wordsFound = new ArrayList<String>() {
            @Override public String toString()
            {
                StringBuilder s = new StringBuilder();
                if (size() >= 1) {
                    s = new StringBuilder(get(0));
                } else {
                    return s.toString();
                }

                for (int i = 1; i < size(); i++) {
                    s.append(", ").append(get(i));
                }
                return s.toString();
            }
        };
    }

    void updateScore(String word) {
        score += 2 * word.length() - 2;
        
    }
    
    int getScore(){
        return score;
    }

    boolean isWordAlreadyFound(String word) {
        return wordsFound.contains(word);
    }

    void addFoundWord(String word){
        wordsFound.add(word);
    }

    String getFoundWords(){
        return wordsFound.toString();
    }

    void reset() {
        score = 0;
        wordsFound.clear();
    }
}
