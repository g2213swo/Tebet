package me.g2213swo.tebet;

public enum Feeling {
    /**
     * Json要求有下面的属性：
     * “content”：String类型，代表输出的结果
     * “feeling”：int类型，代表着你对话的时候的情绪，
     * 其中“0”代表平衡，
     * “-1”代表无聊，
     * “-2”代表难过，
     * "-3"代表烦躁，
     * “1”代表开心，
     * “2”代表激动。
     */
    ANGRY(-3), //-3  0

    SAD(-2), //-2  1

    BORED(-1), //-1  2

    BALANCE(0), //0  3

    HAPPY(1), //1  4

    EXCITED(2); //2  5
    private final int feeling;

    Feeling(int feeling){
        this.feeling = feeling;
    }
    public String getFeelingName() {
        return this.name().toLowerCase();
    }

    public static Feeling getFeeling(int x) {
        for (Feeling feeling : Feeling.values()){
            if (x == feeling.feeling) return feeling;
        }
        return null;
    }
}
