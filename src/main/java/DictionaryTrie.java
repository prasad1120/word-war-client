
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Prasad
 */
class DictionaryTrie {

    private TrieNode root;
    static char[][] board = new char[4][4];
    ArrayList<String> allWordsFromBoard = new ArrayList<String>() {
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

    DictionaryTrie() {
        root = new TrieNode(' ');
        try {

            InputStream inputStream = this.getClass().getResourceAsStream("dictionary.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));;
            String line = reader.readLine();

            while(line != null){
                insert(line);
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Function to insert word */
    public void insert(String word) {
        if (search(word) == TrieSearchResult.WORD) {
            return;
        }
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            TrieNode child = current.subNode(ch);
            if (child != null) {
                current = child;
            } else {
                TrieNode node = new TrieNode(ch);
                current.childList.add(node);
                current = node;
            }
            current.count++;
        }
        current.isEnd = true;
    }

    /* Function to search for word */
    public TrieSearchResult search(String word) {
        String lowerCasedWord = word.toLowerCase();
        TrieNode current = root;
        for (char ch : lowerCasedWord.toCharArray()) {
            if (current.subNode(ch) == null) {
                return TrieSearchResult.NULL;
            } else {
                current = current.subNode(ch);
            }
        }
        if (current.isEnd) {
            return TrieSearchResult.WORD;
        }
        return TrieSearchResult.SUFFIX;
    }

    ArrayList<String> getAllWordsFromBoard(String randomString) {
        allWordsFromBoard.clear();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = randomString.charAt(i * 4 + j);
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                boolean[][] visited = new boolean[4][4];
                getWordsFromPosition("", i, j, visited);
            }
        }
        return allWordsFromBoard;
    }


    void getWordsFromPosition(String prefix, int i, int j, boolean[][] visited) {

        if (i < 0 || j < 0 || i >= 4 || j >= 4 || visited[i][j]) {
            return;
        }
        prefix += board[i][j];

        if (search(prefix) == TrieSearchResult.NULL) {
            return;
        }
        if (search(prefix) == TrieSearchResult.WORD) {
            if (!allWordsFromBoard.contains(prefix)) {
                allWordsFromBoard.add(prefix);
            }
        }

        visited[i][j] = true;

        for (int p = i-1; p <= i+1; p++) {
            for (int q = j-1; q <= j+1; q++) {
                if (p == i && q == j) {
                    continue;
                }
                getWordsFromPosition(prefix, p, q, visited);
            }
        }
        visited[i][j] = false;
    }
}

class TrieNode {

    char content;
    boolean isEnd;
    int count;
    LinkedList<TrieNode> childList;

    TrieNode(char c) {
        childList = new LinkedList<>();
        isEnd = false;
        content = c;
        count = 0;
    }

    TrieNode subNode(char c) {
        if (childList != null) {
            for (TrieNode eachChild : childList) {
                if (eachChild.content == c) {
                    return eachChild;
                }
            }
        }
        return null;
    }
}

enum TrieSearchResult {
    SUFFIX, WORD, NULL
}