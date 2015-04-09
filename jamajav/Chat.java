package jamajav;

// For resizable arrays:
import java.util.ArrayList;

class Chat {
    private ArrayList<ChatEntry> entries;

    public int getSize() {
        return entries.size();
    }

    public ChatEntry get(int i) {
        return entries.get(i);
    }

    public void clear() {
        entries.clear();
    }

    public void add(ChatEntry entry) {
        entries.add(entry);
    }

    Chat() {
        entries = new ArrayList<ChatEntry>(0);
    }
}


