package spell;

public class Trie implements ITrie {
    int wordCount;
    int nodeCount;
    public INode root;

    public Trie() {
        wordCount = 0;
        nodeCount = 1;
        root = new TrieNode(0);
    }

    @Override
    public void add(String word) {
        INode currNode = root;
        word = word.toLowerCase();

        // for each letter in word
        for(int letterNum = 0; letterNum < word.length(); letterNum++) {
            int letterOffsetIndex = (int)(word.charAt(letterNum) - 'a');
            INode[] children = currNode.getChildren();

            // if the current node at the index represented by the letter is null
            if(children[letterOffsetIndex] == null) {
                // insert a new node at that index with value of 0
                children[letterOffsetIndex] = new TrieNode(0);
                nodeCount++;
            }
            // make current node the node at the letter index
            currNode = children[letterOffsetIndex];

            // if it is the last letter in the word, increment the count
            if(letterNum == (word.length() - 1)) {
                if(currNode.getValue() < 1) {
                    wordCount++;
                }
                currNode.incrementValue();
            }
        }
    }

    @Override
    public INode find(String word) {
        INode currNode = root;
        word = word.toLowerCase();

        // for each letter in the word
        for(int letterNum = 0; letterNum < word.length(); letterNum++) {
            int letterOffsetIndex = (int)(word.charAt(letterNum) - 'a');
            INode[] children = currNode.getChildren();

            if(children[letterOffsetIndex] == null) {
                return null;
            } else {
                currNode = children[letterOffsetIndex];
            }
        }

        if(currNode.getValue() >= 1) {
            return currNode;
        } else {
            // word count < 1, so it's only a partial word on the trie
            return null;
        }
    }

    @Override
    public int getWordCount() {
        return wordCount;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public int hashCode() {
        INode[] children = root.getChildren();

        // start with prime number
        int hash = 7;
        hash = hash * 31 + nodeCount;
        hash = hash * 31 + wordCount;
        for(int i = 0; i < children.length; i++) {
            if(root.getChildren()[i] != null) {
                hash = hash + i;
            }
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(obj == this) {
            return true;
        }

        if(!obj.getClass().equals(this.getClass())) {
            return false;
        }

        Trie dictionary = (Trie)obj;

        if(dictionary.getWordCount() != this.getWordCount() || dictionary.getNodeCount() != this.getNodeCount()) {
            return false;
        }

        return equalsHelper(this.root, dictionary.root);
    }

    private boolean equalsHelper(INode n1, INode n2) {
        INode[] children1 = n1.getChildren();
        INode[] children2 = n2.getChildren();

        // do the nodes have the same count?
        if(n1.getValue() != n2.getValue()) {
            return false;
        }
        // do they have non-null children in the same indexes?
        for(int i = 0; i < children1.length; i++) {
            if(children1[i] != null && children2[i] == null) {
                return false;
            }
            if(children2[i] != null && children1[i] == null) {
                return false;
            }
        }

        // now that all 26 indexes have been confirmed to be the same, iterate breadth-first on its children
        boolean branchBool = true;
        for(int i = 0; i < children1.length; i++) {
            if(children1[i] != null) {
                if(!equalsHelper(children1[i], children2[i])) {
                    branchBool = false;
                }
            }
        }

        // nothing returned false after recursing over everything, so they are the same
//        System.out.println("n1.getValue: " + n1.getValue());
//        System.out.println("n2.getValue: " + n2.getValue());
        return branchBool;
    }

    @Override
    public String toString() {
        StringBuilder currWord = new StringBuilder();
        StringBuilder words = new StringBuilder();

        toStringHelper(root, currWord, words);
        return words.toString();
    }

    private void toStringHelper(INode currNode, StringBuilder currWord, StringBuilder words) {
        if(currNode.getValue() > 0) {
            words.append(currWord.toString());
            words.append("\n");
        }

        INode[] children = currNode.getChildren();

        for(int i = 0; i < children.length; i++) {
            if(children[i] != null) {
                char c = (char)('a' + i);
                currWord.append(c);
                toStringHelper(children[i], currWord, words);

                currWord.deleteCharAt(currWord.length() - 1);
            }
        }
    }
}
