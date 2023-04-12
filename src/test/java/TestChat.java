import me.g2213swo.tebet.model.ChatUser;

public class TestChat {
    public static void main(String[] args) {
        ChatUser.ChatUserBuilder builder =  new ChatUser.ChatUserBuilder();
        builder.setQQ(1L);
        builder.setMessage("test");
        builder.setAngry(true);
        System.out.println(builder.build());
    }
}
