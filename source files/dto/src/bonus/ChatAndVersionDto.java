package bonus;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatAndVersionDto {
        private int version;
        private SingleChatEntry[] entries;

    public ChatAndVersionDto(List<SingleChatEntry> entries, int version) {
        setEntries(entries);
        this.version = version;
    }

        public int getVersion() {
            return version;
    }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<SingleChatEntry> getEntries() {
            return new ArrayList<>(Arrays.asList(entries));
        }

        public void setEntries(List<SingleChatEntry> entries) {
            SingleChatEntry[] res=new SingleChatEntry[entries.size()];
            int i=0;
            for(SingleChatEntry s:entries){
                res[i]=s;
                i++;
            }
            this.entries = res;
        }
}

