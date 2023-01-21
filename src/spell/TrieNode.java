package spell;

public class TrieNode implements INode {
    private int value;
    private INode[] children;

    public TrieNode(int value) {
        this.value = value;
        children = new TrieNode[26];
    }

    public TrieNode() {
        this.value = 0;
        children = new TrieNode[26];
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void incrementValue() {
        value++;
//        System.out.println("incrementing value from " + (value - 1) + "to " + value);
    }

    @Override
    public INode[] getChildren() {
        return children;
    }
}

